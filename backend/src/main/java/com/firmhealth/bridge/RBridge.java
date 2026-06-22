import java.net.http.*;
import java.net.URI;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class RBridge{
    @Value("${firmhealth.r-analytics.url}")
    private String rURL;
    @Value("${firmhealth.python-service.url}")
    private String pyFallback;

    public String analyzeTrends(String ticker, Map<String,Object> ratios){
        try{
            ObjectMapper mapper=new ObjectMapper();//serioalization of input map to json string
            String json = mapper.writeValueAsString(ratios);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder() 
            .uri(URI.create(rURL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            return responseBody;
        }catch(Exception e){
            System.err.println("The [RBridge] Plumber Call failed: "+e.getMessage());
            try{
                ObjectMapper mapper=new ObjectMapper();//serioalization of input map to json string
                String json = mapper.writeValueAsString(ratios);
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder() 
                .uri(URI.create(pyFallback+"/trends"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            return responseBody;
                
            }catch(Exception e2){
                System.err.println("The [python fallback] call also failed: "+e2.getMessage());
                return null;
            }
        }
    }
}