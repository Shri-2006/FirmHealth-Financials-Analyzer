#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "ratios.h"
#include "cJSON.h"
#define BUFFER_SIZE 4096

double computeCurrentRatio(FinancialInputs *inputs){
    if(inputs->currentLiabilities ==0.0){
        return RATIO_ERROR;
    }
    else{
        double res = (inputs->currentAssets)/(inputs->currentLiabilities);
        return res;
    }
}

double computeDebtToEquity(FinancialInputs *inputs){
    if(inputs->totalEquity==0.0){
        return RATIO_ERROR;
    }else{
        double res = (inputs->totalDebt)/(inputs->totalEquity);
        return res;
    }
}

double computeProfitMargin(FinancialInputs *inputs){
    if(inputs->revenue==0.0){
        return RATIO_ERROR;
    }
    else{
        double res=(inputs->netIncome)/(inputs->revenue);
        return res;
    }
}

double computeInterestCoverage(FinancialInputs *inputs){
    if(inputs->interestExpense==0.0){
        return RATIO_ERROR;
    }
    else{
        double res=(inputs->ebit)/(inputs->interestExpense);
        return res;
    }
}

double computeAltmanZ(FinancialInputs *inputs){
    if(inputs->totalAssets==0.0){
        return RATIO_ERROR;
    }
    else{
        double res=(1.2*(inputs->workingCapital)/(inputs->totalAssets))+ ((1.4)*(inputs->retainedEarnings)/(inputs->totalAssets))+(3.3*(inputs->ebit)/(inputs->totalAssets))+((0.6)*(inputs->marketCapEquity/inputs->totalLiabilities))+(inputs->sales/(inputs->totalAssets));
        return res;
    }
}

int main(){
    char input[BUFFER_SIZE];
    fgets(input,BUFFER_SIZE,stdin);
    cJSON *parsed=cJSON_Parse(input);
    FinancialInputs inputs;
    inputs.currentAssets=cJSON_GetObjectItem(parsed, "currentAssets")->valuedouble;
    inputs.currentLiabilities=cJSON_GetObjectItem(parsed, "currentLiabilities")->valuedouble;
    inputs.totalDebt=cJSON_GetObjectItem(parsed, "totalDebt")->valuedouble;
    inputs.totalEquity=cJSON_GetObjectItem(parsed, "totalEquity")->valuedouble;
    inputs.netIncome=cJSON_GetObjectItem(parsed, "netIncome")->valuedouble;
    inputs.revenue=cJSON_GetObjectItem(parsed, "revenue")->valuedouble;
    inputs.ebit=cJSON_GetObjectItem(parsed, "ebit")->valuedouble;
    inputs.interestExpense=cJSON_GetObjectItem(parsed, "interestExpense")->valuedouble;
    inputs.workingCapital=cJSON_GetObjectItem(parsed, "workingCapital")->valuedouble;
    inputs.totalAssets=cJSON_GetObjectItem(parsed, "totalAssets")->valuedouble;
    inputs.retainedEarnings=cJSON_GetObjectItem(parsed, "retainedEarnings")->valuedouble;
    inputs.marketCapEquity=cJSON_GetObjectItem(parsed, "marketCapEquity")->valuedouble;
    inputs.totalLiabilities=cJSON_GetObjectItem(parsed, "totalLiabilities")->valuedouble;
    inputs.sales=cJSON_GetObjectItem(parsed, "sales")->valuedouble;
    
    double currentRatio=computeCurrentRatio(&inputs);
    double debtToEquity=computeDebtToEquity(&inputs);
    double profitMargin=computeProfitMargin(&inputs);
    double interestExpense=computeInterestCoverage(&inputs);
    double altmanZ=computeAltmanZ(&inputs);

    cJSON *output=cJSON_CreateObject();

    cJSON_AddNumberToObject(output, "currentRatio",currentRatio);
    cJSON_AddNumberToObject(output, "debtToEquity",debtToEquity);
    cJSON_AddNumberToObject(output, "profitMargin",profitMargin);
    cJSON_AddNumberToObject(output, "interestCoverage",interestCoverage);
    cJSON_AddNumberToObject(output, "altmanZ",altmanZ);
    char *printer=cJSON_Print(output);
    printf("%s\n",printer);
    cJSON_Delete(output);
    cJSON_Delete(parsed);
    free(printer);
    return 0;
}
