export interface FromToDatePair {
    FromDate: YearMonth | null;
    ToDate: YearMonth | null;
}

export interface YearMonth {
	year: number;
	month: number;
}
