import { APIGatewayProxyEvent } from 'aws-lambda';
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

export const grab = async (event: APIGatewayProxyEvent) => {
	const spotifyCredentials = await secretsManagerClient
		.send(
			new GetSecretValueCommand({
				SecretId:
					'arn:aws:secretsmanager:eu-central-1:277817539157:secret:spotify_secrets-t94O8R',
			})
		)
		.then((res) => {
			if (res.SecretString !== undefined && res.SecretString !== '')
				return JSON.parse(res.SecretString);
			else
				throw new Error(
					'Spotify Credentials could not be obtained from Secret'
				);
		});

	const spotifyClient = new SpotifyWebApi({
		clientId: spotifyCredentials['spotify-api-client-id'],
		clientSecret: spotifyCredentials['spotify-api-secret'],
	});

	await spotifyClient.clientCredentialsGrant().then((data) => {
		spotifyClient.setAccessToken(data.body['access_token']);
	});

	let spotifySearchOffset = 0;

	while (true) {
		try {
			let songNameList: string[] = [];

			await spotifyClient
				.searchTracks('year:2020', {
					limit: 50,
					offset: spotifySearchOffset,
					market: 'DE',
				})
				.then((data) => {
					if (data.body.tracks?.items === undefined)
						throw new Error('No tracks found in response');

					for (let track of data.body.tracks?.items) {
						songNameList.push(track.name);
					}
				});

			await s3Client.send(
				new PutObjectCommand({
					Bucket: 'frhetorix-analysis',
					Key: `search_${2020}_DE_track_${new Date()
						.toLocaleDateString()
						.replaceAll('/', '-')}_${spotifySearchOffset}.json`,
					Body: JSON.stringify(songNameList),
					ContentType: 'text/plain',
				})
			);

			spotifySearchOffset += 50;
		} catch (e) {
			console.log(e);
			break;
		}
	}
};
