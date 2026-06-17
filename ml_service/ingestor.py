import requests
import yfinance as yf
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
        facts_data=requests.get(url).json()
        us_gaap=facts_data['facts']['us-gaap']
        metrics={}
        for metric, concepts in CONCEPT_MAP.items():
            if not concepts:
                continue
            for concept in concepts:
                if(concept in us_gaap):
                    all_entries=us_gaap[concept]['units']['USD']
                    filtered=[e for e in all_entries if e['form']=='10-K']
                    metrics[metric]=max(filtered,key=lambda x: x['end'])['val']
                    break
            else:
                metrics[metric]=None        
                    
        if(metrics['currentAssets'] is None or metrics['currentLiabilities'] is None):
            metrics['workingCapital']=None
        else:
            metrics['workingCapital']=metrics['currentAssets']-metrics['currentLiabilities']
        metrics['sales']=metrics['revenue']
        metrics['marketCapEquity']=None
        return metrics
    except:
        return None

def fetch_yahoo_metrics(ticker):
    
    info=yf.Ticker(ticker).info
    metrics={}
    try:
        metrics['revenue']=info.get('totalRevenue')
        metrics['netIncome']=info.get('netIncomeToCommon')
        metrics['totalAssets']=info.get('totalAssets')
        metrics['totalLiabilities']=info.get('totalDebt')
        bv = info.get('bookValue')
        shares = info.get('sharesOutstanding')
        metrics['totalEquity'] = bv * shares if (bv is not None and shares is not None) else None
        metrics['currentAssets']=info.get('currentAssets')
        metrics['currentLiabilities']=info.get('currentLiabilities')
        metrics['totalDebt']=info.get('totalDebt')
        metrics['retainedEarnings']=info.get('retainedEarnings')
        metrics['ebit']=info.get('ebit')
        metrics['interestExpense']=info.get('interestExpense')
        metrics['marketCapEquity']=info.get('marketCap')    
        if(metrics['currentAssets'] is None or metrics['currentLiabilities'] is None):
            metrics['workingCapital']=None
        else:
            metrics['workingCapital']=metrics['currentAssets']-metrics['currentLiabilities']
        metrics['sales']=metrics['revenue']
        return metrics
    except:
        return None

def fetch_metrics(ticker):
    try:
        metrics=fetch_edgar_metrics(ticker)
        if metrics is None:
            metrics=fetch_yahoo_metrics(ticker)
        return metrics
    except:
        return None