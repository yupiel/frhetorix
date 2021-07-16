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

interface WordsWithFrequencies {
	[key: string]: number;
}
