package com.firmhealth.api;
import com.firmhealth.dto.AnalyzeRequestDTO;
import com.firmhealth.dto.AnalyzeResponseDTO;
import com.firmhealth.service.AnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

//this is the rest controller, which is the public facing side of the api. it recieves http requests, delegates to analysis service, returns responses and routes and delegates. Absolutely no business logic

@RestController
@RequestMapping("/api")
public class AnalysisController{
    //Spring will inject a new analysis service so we dont have to make it every single t ime
    private final AnalysisService analysisService;
    public AnalysisController(AnalysisService analysisService) {
        this.analysisService=analysisService;
    }

    //this will trigger analysis pipeline per ticker
    @PostMapping("/analyze")
    public ResponseEntity<AnalyzeResponseDTO> analyze(@Valid @RequestBody AnalyzeRequestDTO request) {
        AnalyzeResponseDTO response = analysisService.analyze(request.getTicker());
        return ResponseEntity.ok(response);
    }
    //retrieval of the report for a ticker that was generated
    @GetMapping("/report/{ticker}")
    public ResponseEntity<AnalyzeResponseDTO> getReport(@PathVariable String ticker) {
        AnalyzeResponseDTO response=analysisService.getReport(ticker);
        return ResponseEntity.ok(response);
    }
    //get the raw financialm metrics for a ticker
    @GetMapping("/company/{ticker}")
    public ResponseEntity<Map<String,Double>> getMetrics(@PathVariable String ticker) {
        Map<String,Double> metrics=analysisService.getMetrics(ticker);
        return ResponseEntity.ok(metrics);
    }
    //deelte stored report
    @DeleteMapping("/report/{ticker}")
     public ResponseEntity<Void>deleteReport(@PathVariable String ticker) {
        analysisService.deleteReport(ticker);
        return ResponseEntity.noContent().build();
    }

}