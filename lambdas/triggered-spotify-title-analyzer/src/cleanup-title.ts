import stopword from 'stopword';

export const cleanupTitle = async (
	inputWordString: string,
	detectedLanguage: string | null
): Promise<Array<string>> => {
	const removedNewLines: string = inputWordString.replace(
		'[\\t\\n\\r]+',
		' '
	);
	const removedPunctuation: string = removedNewLines.replace(
		/[^a-zA-Z ]/g,
		''
	);

	const splitWords: Array<string> = [];

	for (let word of removedPunctuation.split(' ')) {
		word = word.toLowerCase();
		word = word.trim();

		if (word === '' || word === ' ') continue;

		splitWords.push(word);
	}


	if(detectedLanguage !== null){
		const cleanedWords = stopword.removeStopwords(
			splitWords,
			stopword[detectedLanguage]
		);
		return cleanedWords;
	}

	return splitWords;
};
