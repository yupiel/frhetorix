import dateFormat from 'dateformat';
import { ModelCtor } from 'sequelize';
import { AnalysisInformation } from '../types/analysis-information';
import { AnalysisEntry, AnalysisEntryAttributes } from '../types/database-entry';
import { ResponseData, TrackSearchItem } from '../types/spotify-response';
import { cleanupTitle } from './cleanup-title';
import { getLanguageOfTrack } from './get-language-of-track';
import { getWordFrequency } from './get-word-frequency';

export async function batchAnalyzer(
	spotifyResponseBatch: ResponseData,
	analyisEntryModel: ModelCtor<AnalysisEntry>
): Promise<Array<AnalysisEntryAttributes>> {
	const newDatasetsToAdd: Array<AnalysisEntryAttributes> = [];
	const marketString = getMarketFromAPICallLink(
		spotifyResponseBatch.body.tracks.href
	);
	console.log(`Batch market detected as ${marketString}`);

	for (let itemEntry of spotifyResponseBatch.body.tracks.items) {
		console.log(
			`Checking if Track ID ${itemEntry.id} exists in database already...`
		);
		const itemIDEntryExists = await analyisEntryModel.findOne({
			where: { TrackID: itemEntry.id },
		});

		if (itemIDEntryExists !== null) continue;

		console.log(
			`Track ID ${itemEntry.id} did not exist in database. Analyzing...`
		);
		const entryDataAnalysis = await getAnalysisInformationFromEntry(
			itemEntry
		);

		if (entryDataAnalysis === null) continue;

		for (let analysisResult of entryDataAnalysis) {
			newDatasetsToAdd.push({
				Market: marketString.toLowerCase(),
				TrackMonth: analysisResult.trackMonth,
				TrackYear: analysisResult.trackYear,
				DetectedLanguage: analysisResult.detectedLanguage,
				TrackID: analysisResult.trackID,
				Word: analysisResult.word,
				Occurences: analysisResult.occurences,
			});
		}
	}

	return newDatasetsToAdd;
}

function getMarketFromAPICallLink(link: string) {
	if (!link.includes('&market='))
		throw new Error('Link does not contain market info');

	return link.split('&market=')[1].split('&')[0];
}

async function getAnalysisInformationFromEntry(
	spotifyTrackEntry: TrackSearchItem
): Promise<Array<AnalysisInformation> | null> {
	const analyzedResultSet: Array<AnalysisInformation> = [];

	const detectedLanguage = await getLanguageOfTrack(spotifyTrackEntry);
	if (
		detectedLanguage !== null &&
		detectedLanguage !== 'de' &&
		detectedLanguage !== 'en'
	)
		return null;

	console.log(`Track language detected as ${detectedLanguage}`);

	console.log('Cleaning up song title...');
	const cleanedUpWordSet = await cleanupTitle(
		spotifyTrackEntry.name,
		detectedLanguage
	);

	const analyzableWordSet = getWordFrequency(cleanedUpWordSet);

	const trackDate = new Date(spotifyTrackEntry.album.release_date);

	for (let cleanWord in analyzableWordSet) {
		analyzedResultSet.push({
			trackMonth: parseInt(dateFormat(trackDate, 'mm')),
			trackYear: parseInt(dateFormat(trackDate, 'yyyy')),
			detectedLanguage: detectedLanguage!,
			trackID: spotifyTrackEntry.id,
			word: cleanWord,
			occurences: analyzableWordSet[cleanWord],
		});
	}

	return analyzedResultSet;
}
