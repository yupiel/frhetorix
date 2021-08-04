import { APIGatewayProxyEventMultiValueQueryStringParameters } from 'aws-lambda';

export interface SearchParameters {
	Market: string[] | null;
	TrackMonth: string[] | null;
	TrackYear: string[] | null;
	DetectedLanguage: string[] | null;
	Word: string[] | null;
}

export const fromAPIGatewayParameter = (
	parameters: APIGatewayProxyEventMultiValueQueryStringParameters | null
): SearchParameters => {
	try {
		if (parameters === null) throw new Error('No parameters found');

		const returnSearchParameters: SearchParameters = {} as SearchParameters;

		for (let paramKey in parameters) {
            //sanity checking start
			if (!Object.keys(possibleQueryParameters).includes(paramKey)) {
				console.error(
					`Unknown parameter ${paramKey} was passed... ignoring`
				);
				continue;
			}
			if (parameters[paramKey]![0] === '') {
				console.error(
					`Parameter ${paramKey} was passed without value... ignoring`
				);
				continue;
			}

			let cleanedParameter = parameters[paramKey];
			if (cleanedParameter!.length === 1) {
				cleanedParameter = cleanedParameter![0].split(',');
			}

			cleanedParameter = cleanedParameter!.filter((el) => el != '');
            //sanity checking end

			returnSearchParameters[possibleQueryParameters[paramKey]] =
				cleanedParameter;
		}

		return returnSearchParameters;
	} catch (err) {
		console.error(err);
		throw new Error('Search Parameters couldn not be parsed');
	}
};

const possibleQueryParameters = {
	markets: 'Market',
	months: 'TrackMonth',
	years: 'TrackYear',
	languages: 'DetectedLanguage',
	words: 'Word',
};
