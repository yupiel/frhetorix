import { SearchParameters } from '../types/search-parameters';
import { Op, WhereOptions } from 'sequelize';
import { AnalysisEntryAttributes } from '../types/database-entry';

export const createSequelizeWhereParameterAtributes = (
	searchParameters: SearchParameters
): WhereOptions<AnalysisEntryAttributes> => {
	let whereAttributes = {} as WhereOptions<AnalysisEntryAttributes>;

	for (let paramKey in searchParameters) {
		if (searchParameters[paramKey] === null) continue;

		if ((searchParameters[paramKey] as string[]).length === 1) {
			whereAttributes[paramKey] = searchParameters[paramKey][0];
		} else if (paramKey === 'FromToDatePair') {
			let whereTrackMonth = {};
			let whereTrackYear = {};

			if (
				searchParameters[paramKey].FromDate &&
				searchParameters[paramKey].FromDate?.month &&
				searchParameters[paramKey].FromDate?.year
			) {
				Object.assign(whereTrackMonth, {
					[Op.gte]: searchParameters[paramKey].FromDate?.month,
				});
				Object.assign(whereTrackYear, {
					[Op.gte]: searchParameters[paramKey].FromDate?.year,
				});
			}

			if (
				searchParameters[paramKey].ToDate &&
				searchParameters[paramKey].ToDate?.month &&
				searchParameters[paramKey].ToDate?.year
			) {
				Object.assign(whereTrackMonth, {
					[Op.lte]: searchParameters[paramKey].ToDate?.month,
				});
				Object.assign(whereTrackYear, {
					[Op.lte]: searchParameters[paramKey].ToDate?.year,
				});
			}

			if (Object.keys(whereTrackMonth).length !== 2) {
				Object.assign(whereAttributes, {
					[Op.and]: {
						[Op.and]: {
							TrackMonth: whereTrackMonth,
							TrackYear: whereTrackYear,
						},
					},
				});
			} else {
				Object.assign(whereAttributes, {
					[Op.and]: {
						TrackMonth: whereTrackMonth,
						TrackYear: whereTrackYear,
					},
				});
			}
		} else {
			whereAttributes[paramKey] = { [Op.or]: searchParameters[paramKey] };
		}
	}

	return whereAttributes;
};
