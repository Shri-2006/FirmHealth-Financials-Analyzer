package com.firmhealth.service;
import com.firmhealth.dto.AnalyzeResponseDTO;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import com.firmhealth.bridge.CBridge;
//This is intended to contain all the business logic for financial analysis, the cotnroller will call these methods and this class does the actual work

@Service
public class AnalysisService{

    private final CBridge cBridge;
    public AnalysisService(CBridge cBridge){
        this.cBridge=cBridge;
    }

    private Map<String, Double> computeRatiosFallback(Map<String, Double> inputs) {
    Map<String, Double> ratios = new HashMap<>();
    
    double currentAssets = inputs.getOrDefault("currentAssets", 0.0);
    double currentLiabilities=inputs.getOrDefault("currentLiabilities",0.0);
    double totalDebt=inputs.getOrDefault("totalDebt",0.0);
    double totalEquity=inputs.getOrDefault("totalEquity",0.0);
    double netIncome=inputs.getOrDefault("netIncome",0.0);
    double revenue=inputs.getOrDefault("revenue",0.0);
    double ebit=inputs.getOrDefault("ebit",0.0);
    double interestExpense=inputs.getOrDefault("interestExpense",0.0);
    double workingCapital=inputs.getOrDefault("workingCapital",0.0);
    double totalAssets=inputs.getOrDefault("totalAssets",0.0);
    double retainedEarnings=inputs.getOrDefault("retainedEarnings",0.0);
    double marketCapEquity=inputs.getOrDefault("marketCapEquity",0.0);
    double totalLiabilities=inputs.getOrDefault("totalLiabilities",0.0);
    double sales=inputs.getOrDefault("sales",0.0);
    
    // currentRatio
    ratios.put("currentRatio", currentLiabilities == 0.0 ? -1.0 : currentAssets / currentLiabilities);
    
    //debttoequity
    ratios.put("debtToEquity",totalEquity==0.0 ?-1.0 : totalDebt/totalEquity);

    //profit margin
    ratios.put("profitMargin",revenue==0.0 ? -1.0 : netIncome/revenue);

    //interest coverage
    ratios.put("interestCoverage",interestExpense==0.0 ? -1.0: ebit/interestExpense);

    //altmanz
    ratios.put("altmanZ", totalAssets==0.0 ? -1.0 : (((1.2*(workingCapital)/(totalAssets))+ ((1.4)*(retainedEarnings)/(totalAssets))+(3.3*(ebit)/(totalAssets))+((0.6)*(marketCapEquity/totalLiabilities))+(sales/(totalAssets)))));
    
    return ratios;
}
    //This will run the full analysis pipeline for a specific ticker. current plans include python C and R services
    public AnalyzeResponseDTO analyze(String ticker){
        //this is a placholder until we get a pipeline running
        AnalyzeResponseDTO response=new AnalyzeResponseDTO();
        response.setTicker(ticker);
        response.setHealthScore(0);
        response.setDistressFlag(false);
        response.setCreditRecommendation("Pending");
        response.setStatus("Analysis pipeline not yet connected");
        return response;
    }
    //now to retrieve the actual generated report for tickers
    public AnalyzeResponseDTO getReport(String ticker) {
        // Stub for now
        AnalyzeResponseDTO response=new AnalyzeResponseDTO();
        response.setTicker(ticker);
        response.setStatus("Report retrieval not yet implemented");
        return response;
    }
    //this will be the raw extracted metrics for analysis
     public Map<String, Double> getMetrics(String ticker) {
        //temproary stub
        Map<String, Double> inputMap = new HashMap<>();
        inputMap.put("currentAssets", 150000.0);
        inputMap.put("currentLiabilities", 80000.0);
        inputMap.put("totalDebt", 200000.0);
        inputMap.put("totalEquity", 400000.0);
        inputMap.put("netIncome", 50000.0);
        inputMap.put("revenue", 300000.0);
        inputMap.put("ebit", 70000.0);
        inputMap.put("interestExpense", 15000.0);
        inputMap.put("workingCapital", 70000.0);
        inputMap.put("totalAssets", 600000.0);
        inputMap.put("retainedEarnings", 120000.0);
        inputMap.put("marketCapEquity", 500000.0);
        inputMap.put("totalLiabilities", 250000.0);
        inputMap.put("sales", 300000.0);
        Map<String,Double>res=cBridge.computeRatios(inputMap);
        if(res==null){
            System.err.println("[AnalysisService]C engine is unavailable, falling back to java failsafe");
            res=computeRatiosFallback(inputMap);
        }
        return res;
        
    } 
    public void deleteReport(String ticker){
        //stub for now, will be replaced with a delete instance  
    }
}