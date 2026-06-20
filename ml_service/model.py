import joblib
import os
def load_model():
    try:
        if os.path.exists('model.pkl'):
            mod=joblib.load('model.pkl')
            return mod
        else:
            return None
    except:
        return None

def predict_distress(metrics):
    model=load_model()
    if model is None:
        return {"error":"There is no trained model, run train_model.py first to get the model."}
    currentRatio = metrics['currentRatio'] or 0.0
    profitMargin=metrics['profitMargin'] or 0.0
    altmanZ=metrics['altmanZ'] or 0.0
    debtToEquity=metrics['debtToEquity'] or 0.0
    interestCoverage=metrics['interestCoverage'] or 0.0
    numbers=[[currentRatio, profitMargin,altmanZ,debtToEquity,interestCoverage]]
    first=model.predict(numbers)[0]
    prob=model.predict_proba(numbers)[0][1]
    return {"distressed":bool(first),"confidence":round(float(prob),6)}
