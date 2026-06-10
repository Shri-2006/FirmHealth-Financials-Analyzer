import requests

CONCEPT_MAP={
    "revenue": ["Revenues","RevenueFromContractWithCustomerExcludingAssessedTax"],
    "netIncome":["NetIncomeLoss"],
    "totalAssets":["Assets"],
    "totalLiabilities":["Liabilities"],
    "totalEquity":["StockholdersEquity"],
    "currentAssets":["AssetsCurrent"],
    "currentLiabilities":["LiabilitiesCurrent"],
    "totalDebt":["LongTermDebt"],
    "retainedEarnings":["RetainedEarningsAccumulatedDeficit"],
    "ebit":["OperatingIncomeLoss"],
    "interestExpense":["InterestExpense"],
    "sales":["Revenues","RevenueFromContractWithCustomerExcludingAssessedTax"],
    "workingCapital":[],
    "marketCapEquity":[]
}
def fetch_edgar_metrics(ticker):
    try:
        tickers_data=requests.get("https://www.sec.gov/files/company_tickers.json").json()
        cik=None
        for entry in tickers_data.values():
            if (entry['ticker']==ticker):
                cik=entry['cik_str']
                break
        if cik is None:
            return None
        cik_padded=str(cik).zfill(10)
        url=f"https://data.sec.gov/api/xbrl/companyfacts/CIK{cik_padded}.json"
        facts_data=requests.get(url).json
        metrics={}
        for(int i=0;i<CONCEPT_MAP.items();i++):
            
    except:
        return None
