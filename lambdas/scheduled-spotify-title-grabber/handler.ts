import dateFormat from 'dateformat';
import { ScheduledEvent } from 'aws-lambda';
import SpotifyWebApi from 'spotify-web-api-node';
import {
	SecretsManagerClient,
	GetSecretValueCommand,
} from '@aws-sdk/client-secrets-manager';
import { PutObjectCommand, S3Client } from '@aws-sdk/client-s3';

const AWS_REGION = 'eu-central-1';

const secretsManagerClient = new SecretsManagerClient({
	region: AWS_REGION,
});
const s3Client = new S3Client({
	region: AWS_REGION,
});

const searchYear = 2020;

export const grab = async (event: ScheduledEvent) => {
	console.log('Authenticating against Spotify API...');
	const spotifySecret = await secretsManagerClient
		.send(
			new GetSecretValueCommand({
				SecretId:
					'arn:aws:secretsmanager:eu-central-1:277817539157:secret:frhetorix/spotify-api-YnFAVD',
			})
		)
		.then((res) => res);

	if (
		spotifySecret.SecretString === undefined ||
		spotifySecret.SecretString === ''
	)
		throw new Error(
			'Spotify Credentials could not be obtained from Secret'
		);

	const spotifyCredentials = JSON.parse(spotifySecret.SecretString);

	const spotifyClient = new SpotifyWebApi({
		clientId: spotifyCredentials['spotify-api-client-id'],
		clientSecret: spotifyCredentials['spotify-api-secret'],
	});

	await spotifyClient.clientCredentialsGrant().then((data) => {
		spotifyClient.setAccessToken(data.body['access_token']);
	});

	let spotifySearchOffset = 0;

	console.log('Fetching data from Spotify API...');
	while (true) {
		try {
			const spotifyDataResponse = await spotifyClient
				.searchTracks(`year:${searchYear}`, {
					limit: 50,
					offset: spotifySearchOffset,
					market: 'DE',
				})
				.then((data) => data);

			if (spotifyDataResponse === undefined)
				throw new Error('No tracks found in response');

			await s3Client.send(
				new PutObjectCommand({
					Bucket: 'frhetorix-raw-data',
					Key: `search_${searchYear}_DE_track_${dateFormat(
						new Date(),
						'dd-mm-yyyy'
					)}_${spotifySearchOffset}.json`,
					Body: JSON.stringify(spotifyDataResponse),
					ContentType: 'text/plain',
				})
			);

			spotifySearchOffset += 50;
		} catch (e) {
			console.log(e);
			break;
		}
	}

	console.log(
		`Finishing up... wrote ${spotifySearchOffset / 50} files to s3.`
	);
};
