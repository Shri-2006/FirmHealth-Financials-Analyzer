package com.firmhealth.bridge;
import org.springframework.beans.factory.annotation.Value;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;

@Component
public class CBridge {
    @Value("${firmhealth.c-engine.path}")
    private String C_EXECUTABLE;

    public Map<String, Double> computeRatios(Map<String, Double> financialInputs){
        try{
            ObjectMapper mapper=new ObjectMapper();//serioalization of input map to json string
            String json = mapper.writeValueAsString(financialInputs); 
            //create processor builder with C_Executable path and then call redirecterror stream on it
            ProcessBuilder pb=new ProcessBuilder(C_EXECUTABLE);
            pb.redirectErrorStream(true);
            //start actual process
            Process process=pb.start();

            //write json string to process stdin
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter ( process.getOutputStream()));
            writer.write(json);//write json string
            writer.newLine();//new line to get fgets to next line
            writer.close();//EOF

            BufferedReader reader= new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output=reader.readLine();

            process.waitFor();//wait for C to actually exit completely to prevent potential memory buildup and erros
            
            Map<String,Double> result=mapper.readValue(output,new TypeReference<Map<String,Double>>() {});
            ArrayList<String> nanKeys = new ArrayList<String>();
            for(Map.Entry<String,Double> entry : result.entrySet()){
                if(entry.getValue()==null || Double.isNaN(entry.getValue())){
                    nanKeys.add(entry.getKey());
                    System.err.printf("ratio has a NaN value %s\n",entry.getKey());
                }
            }
            for(int i=0;i<nanKeys.size();i++){
                result.put(nanKeys.get(i),null);
            }
            return result;
            
 
        }
        catch(Exception e){
            System.err.println("[CBridge] C engine failed: " + e.getMessage());
            return null;
        }
    }

} 