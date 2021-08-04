import { Model } from 'sequelize';

export interface AnalysisEntryAttributes {
    Market: string;
    TrackMonth: number;
    TrackYear: number;
    DetectedLanguage: string | null;
    TrackID: string;
    Word: string;
    Occurences: number;
}

export interface AnalysisEntry extends Model<AnalysisEntryAttributes>, AnalysisEntryAttributes {}
