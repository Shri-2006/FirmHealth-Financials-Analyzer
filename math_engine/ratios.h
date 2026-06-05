#ifndef RATIOS_H
#define RATIOS_H




#define RATIO_ERROR -1.0

typedef struct {
    double currentAssets;
    double currentLiabilities;
    double totalDebt;
    double totalEquity;
    double netIncome;
    double revenue;
    double ebit;
    double interestExpense;
    double workingCapital;
    double totalAssets;
    double retainedEarnings;
    double marketCapEquity;
    double totalLiabilities;
    double sales;
}FinancialInputs;

double computeCurrentRatio(FinancialInputs *inputs);
double computeDebtToEquity(FinancialInputs *inputs);
double computeProfitMargin(FinancialInputs *inputs);
double computeInterestCoverage(FinancialInputs *inputs);
double computeAltmanZ(FinancialInputs *inputs);



#endif