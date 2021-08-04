import { DataTypes, Sequelize } from 'sequelize';
import { AnalysisEntry } from '../../types/database-entry';

export = (sequelize: Sequelize, tableName: string) => {
	const AnalysisEntry = sequelize.define<AnalysisEntry>(
		'AnalysisEntry',
		{
			Market: {
				type: DataTypes.STRING(255),
				allowNull: false,
				validate: {
					notEmpty: true,
				},
			},
			TrackMonth: {
				type: DataTypes.INTEGER,
				allowNull: false,
				validate: {
					notEmpty: true,
				},
			},
			TrackYear: {
				type: DataTypes.INTEGER,
				allowNull: false,
				validate: {
					notEmpty: true,
				},
			},
			DetectedLanguage: {
				type: DataTypes.STRING(255),
				allowNull: true,
			},
			TrackID: {
				type: DataTypes.STRING(255),
				allowNull: false,
				validate: {
					notEmpty: true,
				},
			},
			Word: {
				type: DataTypes.STRING(255),
				allowNull: false,
				validate: {
					notEmpty: true,
				},
			},
			Occurences: {
				type: DataTypes.INTEGER,
				allowNull: false,
				validate: {
					notEmpty: true,
				},
			},
		},
		{
			tableName: tableName,
			timestamps: false
		}
	);

	AnalysisEntry.removeAttribute('id');

	return AnalysisEntry;
};
