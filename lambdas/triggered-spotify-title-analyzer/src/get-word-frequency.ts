import {WordsWithFrequencies} from '../types/word-with-frequency'

export const getWordFrequency = (
	inputWords: Array<string>
): WordsWithFrequencies => {
	const frequencyMap: WordsWithFrequencies = inputWords.reduce(
		(acc, curr) => {
			acc[curr] ? acc[curr]++ : (acc[curr] = 1);

			return acc;
		},
		{}
	);

	return frequencyMap;
};
