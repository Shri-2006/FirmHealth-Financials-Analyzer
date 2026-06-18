import pandas as pd
#column_map is given by llm to simplify, i dont want to type it out
COLUMN_MAP = {
    "revenue": "revenue", "total revenue": "revenue", "totalrevenue": "revenue", "net revenue": "revenue",
    "net income": "netIncome", "netincome": "netIncome", "net_income": "netIncome", "netincometocommon": "netIncome",
    "total assets": "totalAssets", "totalassets": "totalAssets", "assets": "totalAssets",
    "total liabilities": "totalLiabilities", "totalliabilities": "totalLiabilities", "liabilities": "totalLiabilities",
    "total equity": "totalEquity", "totalequity": "totalEquity", "stockholders equity": "totalEquity", "equity": "totalEquity",
    "current assets": "currentAssets", "currentassets": "currentAssets", "assetscurrent": "currentAssets",
    "current liabilities": "currentLiabilities", "currentliabilities": "currentLiabilities", "liabilitiescurrent": "currentLiabilities",
    "total debt": "totalDebt", "totaldebt": "totalDebt", "long term debt": "totalDebt", "longtermdebt": "totalDebt",
    "retained earnings": "retainedEarnings", "retainedearnings": "retainedEarnings",
    "ebit": "ebit", "operating income": "ebit", "operatingincomeloss": "ebit",
    "interest expense": "interestExpense", "interestexpense": "interestExpense",
    "sales": "sales",
    "working capital": "workingCapital", "workingcapital": "workingCapital",
    "market cap": "marketCapEquity", "marketcap": "marketCapEquity", "marketcapequity": "marketCapEquity"
}

def sanitize_csv(file):
    try:
        df=pd.read_csv(file)
        df.columns=df.columns.str.lower().str.strip()
        df=df.rename(columns=COLUMN_MAP)
        for col in df.columns:
            df[col]=df[col].str.replace('$','').str.replace(',','')
            df[col]=pd.to_numeric(df[col], errors='coerce') #coerce means to turn anything unable to be converted to NaN always
        metrics={}
        KEYS = ['revenue','netIncome','totalAssets','totalLiabilities','totalEquity',
        'currentAssets','currentLiabilities','totalDebt','retainedEarnings',
        'ebit','interestExpense','marketCapEquity','workingCapital','sales']
        for key in KEYS:
            val=df[key].iloc[0] if key in df.columns else None
            metrics[key]=None if pd.isna(val) else val
        if(metrics['currentAssets'] is None or metrics['currentLiabilities'] is None):
            metrics['workingCapital']=None
        else:
            metrics['workingCapital']=metrics['currentAssets']-metrics['currentLiabilities']
        metrics['sales']=metrics['revenue']
        return metrics

    except:
        return None