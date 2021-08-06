import { APIGatewayProxyEventMultiValueQueryStringParameters } from 'aws-lambda';
import { FromToDatePair, YearMonth } from '../types/date-helper';

export interface SearchParameters {
	Market: string[] | null;
	FromToDatePair: FromToDatePair;
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
			if (
				cleanedParameter!.length === 1 &&
				paramKey !== 'fromdate' &&
				paramKey !== 'todate'
			) {
				cleanedParameter = cleanedParameter![0].split(',');
			}

			cleanedParameter = cleanedParameter!.filter((el) => el != '');
			//sanity checking end

			if (paramKey === 'fromdate' || paramKey === 'todate') {
				if (!cleanedParameter.toString().match(/\d{4}-\d{2}$/gm)) {
					console.error(
						`Parameter ${paramKey} was not in the correct format`
					);
					continue;
				}

				const rawYearMonth: string[] = cleanedParameter[0].split('-');
				const yearMonth: YearMonth = {} as YearMonth;
				yearMonth.year = Number(rawYearMonth[0]);
				yearMonth.month = Number(rawYearMonth[1]);

				if (yearMonth.month <= 0 || yearMonth.month > 12) {
					console.error(
						`The month value cannot be ${yearMonth.month}`
					);
					continue;
				}

				if (!returnSearchParameters[possibleQueryParameters[paramKey]])
					returnSearchParameters[possibleQueryParameters[paramKey]] =
						{};

				if (paramKey === 'fromdate') {
					returnSearchParameters[possibleQueryParameters[paramKey]][
						'FromDate'
					] = yearMonth;
				}
				if (paramKey === 'todate') {
					returnSearchParameters[possibleQueryParameters[paramKey]][
						'ToDate'
					] = yearMonth;
				}
			} else {
				returnSearchParameters[possibleQueryParameters[paramKey]] =
					cleanedParameter;
			}
		}

		return returnSearchParameters;
	} catch (err) {
		console.error(err);
		throw new Error('Search Parameters couldn not be parsed');
	}
};

const possibleQueryParameters = {
	markets: 'Market',
	fromdate: 'FromToDatePair',
	todate: 'FromToDatePair',
	languages: 'DetectedLanguage',
	words: 'Word',
};
