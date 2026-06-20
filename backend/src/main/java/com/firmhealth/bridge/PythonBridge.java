package com.firmhealth.bridge;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class PythonBridge{
    public Map<String, Object> fetchEdgarMetrics(String ticker) {
        try{
            ObjectMapper mapper=new ObjectMapper();
            String jsonBody=mapper.writeValueAsString(Map.of("ticker",ticker));
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5000/ingest/edgar"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            Map<String, Object> res = mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
            return res;
        }catch(Exception e){
            System.err.println("The [PythonBridge] Flask Call failed: "+e.getMessage());
            return null;
        }
    }
    public Map<String, Object>fetchCsvMetrics(String csvPath) {
        return null; // not yet implemented
    }

    
}