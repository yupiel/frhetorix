import {
	GetSecretValueCommand,
	GetSecretValueCommandOutput,
	SecretsManagerClient,
} from '@aws-sdk/client-secrets-manager';
import { APIGatewayProxyEvent } from 'aws-lambda';
import { ModelCtor, Sequelize } from 'sequelize';
import {
	fromAPIGatewayParameter,
	SearchParameters,
} from '../types/search-parameters';
import { createSequelizeWhereParameterAtributes } from './create-sequelize-parameters';
import { AnalysisEntry } from '../types/database-entry';
import AnalysisEntryModel from './model/AnalysisEntry';

const AWS_REGION = 'eu-central-1';
const ANALYSIS_DATABASE_NAME = 'postgres';
const ANALYSIS_TABLE_NAME = 'Frhetorix_Analysis';

const secretsManagerClient = new SecretsManagerClient({
	region: AWS_REGION,
});

export const search = async (event: APIGatewayProxyEvent) => {
	let searchParameters: SearchParameters = fromAPIGatewayParameter(
		event.multiValueQueryStringParameters
	);

	console.log('Fetching Database Info and Credentials...');
	const databaseSecret = await getSecretStringValue(
		'arn:aws:secretsmanager:eu-central-1:277817539157:secret:yupieldb/frhetorix-analysis-db-user-Dv3vEw'
	);

	//establish database connection
	const sequelizeClient = await establishDatabaseConnection(databaseSecret);

	//create model definition
	const analysisEntryModel: ModelCtor<AnalysisEntry> = AnalysisEntryModel(
		sequelizeClient,
		ANALYSIS_TABLE_NAME
	);

	try {
		console.log('Querying Database with request parameters...');
		const queryResult = await analysisEntryModel.findAll({
			attributes: [
				'Word',
				'TrackMonth',
				'TrackYear',
				'DetectedLanguage',
				'Market',
				[
					Sequelize.fn('sum', Sequelize.col('Occurences')),
					'TotalOccurrences',
				],
			],
			where: createSequelizeWhereParameterAtributes(searchParameters),
			group: [
				'AnalysisEntry.Word',
				'AnalysisEntry.TrackMonth',
				'AnalysisEntry.TrackYear',
				'AnalysisEntry.DetectedLanguage',
				'AnalysisEntry.Market',
			],
			order: ['TrackMonth', 'TrackYear'],
		});

		console.log('Query success... returning results...');

		return {
			statusCode: 200,
			body: JSON.stringify(queryResult),
		};
	} catch (err) {
		return {
			statusCode: 502,
			body: JSON.stringify(err),
		};
	}
};

async function getSecretStringValue(
	secretARN: string
): Promise<GetSecretValueCommandOutput> {
	const retrievedSecret = await secretsManagerClient.send(
		new GetSecretValueCommand({
			SecretId: secretARN,
		})
	);

	if (
		retrievedSecret.SecretString === undefined ||
		retrievedSecret.SecretString === ''
	)
		throw new Error(`Obtaining the secret for ARN ${secretARN} failed.`);

	return JSON.parse(retrievedSecret.SecretString);
}

async function establishDatabaseConnection(
	databaseSecretString: GetSecretValueCommandOutput
): Promise<Sequelize> {
	const sequelize = new Sequelize(
		ANALYSIS_DATABASE_NAME,
		databaseSecretString['username'],
		databaseSecretString['password'],
		{
			host: databaseSecretString['host'],
			dialect: 'postgres',
		}
	);

	try {
		await sequelize.authenticate();
		console.log('Database connection has been established successfully.');
	} catch (err) {
		throw err;
	}

	return sequelize;
}
