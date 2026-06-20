import pandas as pd 
import joblib
import os
from sklearn.linear_model import LogisticRegression


if os.path.exists('data/custom_training.csv'):
    df = pd.read_csv('data/custom_training.csv')
    df['debtToEquity'] = 0.0 
    df['interestCoverage'] = 0.0
else:
    df = pd.read_csv('data/american_bankruptcy_sample.csv')
    df['currentRatio']=df['X1']/df['X14']
    df['profitMargin']=df['X6']/df['X16']
    df['altmanZ'] = 1.2*((df['X1']-df['X14'])/df['X10'])+1.4*(df['X15']/df['X10'])+3.3*(df['X12']/df['X10']) +0.6*(df['X8']/df['X17']) +(df['X9']/df['X10'])
    df['debtToEquity']=0.0
    df['interestCoverage']=0.0
#replacing null or NaN with 0.0 for ratio check to be conservative in credit grading. 
df = df.replace([float('inf'), float('-inf')], 0) 
df = df.fillna(0)
df['label']=df['status_label'].apply(lambda x: 1 if x == 'failed' else 0)
X=df[['currentRatio','profitMargin','altmanZ','debtToEquity','interestCoverage']].values
y=df['label'].values
model=LogisticRegression()
model.fit(X,y)
joblib.dump(model,'model.pkl')
print("Model trained and saved to model.pkl")