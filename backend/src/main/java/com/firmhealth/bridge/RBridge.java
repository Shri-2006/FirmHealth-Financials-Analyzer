package com.firmhealth.bridge;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
@Component
public class RBridge {
    @Value("${firmhealth.r-analytics.url}")
    private String rUrl;
    @Value("${firmhealth.python-service.url}")
    private String pyFallbackUrl;


    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();




    public String analyzeTrends(String ticker, Map<String, Object> ratios) {
        try {
            String json = mapper.writeValueAsString(Map.of("ticker", ticker, "ratios", ratios));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(rUrl + "/analyze"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            System.err.println("[RBridge] R Plumber call failed: " + e.getMessage());
            try {
                String json = mapper.writeValueAsString(Map.of("ticker", ticker, "ratios", ratios));
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(pyFallbackUrl + "/trends"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            } catch (Exception e2) {
                System.err.println("[RBridge] Python fallback also failed: " + e2.getMessage());
                return null;
            }
        }
    }
}