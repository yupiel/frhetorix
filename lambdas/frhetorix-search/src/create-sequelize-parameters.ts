import { SearchParameters } from '../types/search-parameters';
import { Op, WhereOptions } from 'sequelize';
import { AnalysisEntryAttributes } from '../types/database-entry';

export const createSequelizeWhereParameterAtributes = (
	searchParameters: SearchParameters
): WhereOptions<AnalysisEntryAttributes> => {
	const whereAttributes = {} as WhereOptions<AnalysisEntryAttributes>;

	for (let paramKey in searchParameters) {
		if (searchParameters[paramKey] === null) continue;

		if ((searchParameters[paramKey] as string[]).length === 1) {
			whereAttributes[paramKey] = searchParameters[paramKey][0];
		} else {
			whereAttributes[paramKey] = { [Op.or]: searchParameters[paramKey] };
		}
	}

	return whereAttributes;
};
