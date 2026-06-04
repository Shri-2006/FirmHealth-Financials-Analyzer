package com.firmhealth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FirmHealthApplication {

    public static void main(String[] args) {
        //this is where the firmhealth backend starts. 
        //Springapplcation.run will start with tkaing Spring context, starts the embedded tomcat and begin listening for HTTP calls
        SpringApplication.run(FirmHealthApplication.class, args);
    }

}