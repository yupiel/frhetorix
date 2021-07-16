import lyricsSearcher from 'lyrics-searcher';
import cld from 'cld';
import { TrackSearchItem } from '../types/spotify-response';

export const getLanguageOfTrack = async (
	spotifyTrackData: TrackSearchItem
): Promise<string | null> => {
	let detectedLanguage;
	try {
		const lyrics = await lyricsSearcher(
			spotifyTrackData.artists[0].name,
			spotifyTrackData.name
		);
		detectedLanguage = await cld.detect(lyrics);

		return detectedLanguage['languages'][0]['code'];
	} catch (e) {
		console.log('No Lyrics for song found...');
	}

	try {
		detectedLanguage = await cld.detect(spotifyTrackData.name);
		return detectedLanguage['languages'][0]['code'];
	} catch (e) {
		console.log('Language detection for song title failed.');
		return null;
	}
};
