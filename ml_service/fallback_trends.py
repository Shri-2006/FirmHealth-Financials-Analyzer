from scipy.stats import linregress
import numpy as np
def analyze_single_trend(values):
    periods=list(range(1,len(values)+1))
    slope, intercept, r_value, p_value, std_err = linregress(periods, values)
    r_squared = r_value ** 2
    std_dev = np.std(values)
    if(slope>0.01):
        direction = "improving"
    
    elif (slope<(-0.01)):
        direction = "declining"
    
    else:
        direction = "stable"
    

    if(std_dev>0.3):
        volatile=True
    
    else:
        volatile=False
    
    ratios_dict={"slope":slope,"r_squared":r_squared,"std_dev":std_dev,"direction":direction,"volatile":volatile}
    return ratios_dict

def analyze_all_trends(ratios_dict):
    results = {name: analyze_single_trend(values) for name, values in ratios_dict.items()}
    return results

def generate_report(ticker,trends):
    directions = [t["direction"] for t in trends.values()]
    overallDirections=max(directions,key=directions.count)
    riskFlag=any(t["direction"] == "declining" and t["volatile"] for t in trends.values())
    summary={"overallDirections":overallDirections,"riskFlag":riskFlag}
    res={"ticker":ticker,"trends":trends,"summary":summary}
    return res