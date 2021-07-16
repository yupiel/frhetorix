import { Readable } from 'stream';
import { Client } from 'pg';
import format from 'pg-format';
import { S3Event } from 'aws-lambda';
import { GetObjectCommand, S3Client } from '@aws-sdk/client-s3';
import {
	SecretsManagerClient,
	GetSecretValueCommand,
	GetSecretValueCommandOutput,
} from '@aws-sdk/client-secrets-manager';

import { ResponseData } from '../types/spotify-response';
import { batchAnalyzer } from './batch-analyzer';

const AWS_REGION = 'eu-central-1';
const ANALYSIS_TABLE_NAME = 'Frhetorix_Analysis';

const s3Client = new S3Client({
	region: AWS_REGION,
});
const secretsManagerClient = new SecretsManagerClient({
	region: AWS_REGION,
});

export const analyze = async (event: S3Event) => {
	if (event.Records[0] === null || event.Records[0] === undefined)
		throw new Error('S3 File did not get passed on lambda trigger');

	const bucketName = event.Records[0].s3.bucket.name;
	const fileKey = event.Records[0].s3.object.key;

	const batchStream = await s3Client.send(
		new GetObjectCommand({
			Bucket: bucketName,
			Key: fileKey,
		})
	);

	if (batchStream.Body instanceof Readable === false)
		throw new Error('GetObjectCommand on batch file failed');

	const batchStringData = await streamToString(batchStream.Body as Readable);
	const batchData: ResponseData = JSON.parse(batchStringData);

	console.log('Fetching Database Info and Credentials...');
	const databaseSecret = await getSecretStringValue(
		'arn:aws:secretsmanager:eu-central-1:277817539157:secret:yupieldb/frhetorix-analysis-db-user-Dv3vEw'
	);

	console.log('Logging into Database...');
	const postgresClient = new Client({
		user: databaseSecret['username'],
		host: databaseSecret['host'],
		database: 'postgres',
		password: databaseSecret['password'],
		port: databaseSecret['port'],
	});
	await postgresClient.connect();

	console.log('Starting batch analysis...');
	const newDataForDatabase = await batchAnalyzer(
		batchData,
		postgresClient,
		ANALYSIS_TABLE_NAME
	);

	console.log('Adding results to database...');
	try {
		await postgresClient.query(
			format(
				`INSERT INTO "${ANALYSIS_TABLE_NAME}" ("Market", "TrackMonth", "TrackYear", "DetectedLanguage", "TrackID", "Word", "Occurences") VALUES %L`,
				newDataForDatabase
			)
		);
	} catch (e) {
		console.error(e);
	}

	console.log('Adding batch analysis to database finished.');
	return;
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

async function streamToString(stream: Readable): Promise<string> {
	return await new Promise((resolve, reject) => {
		const chunks: Uint8Array[] = [];
		stream.on('data', (chunk) => chunks.push(chunk));
		stream.on('error', reject);
		stream.on('end', () =>
			resolve(Buffer.concat(chunks).toString('utf-8'))
		);
	});
}
