package com.firmhealth.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

//data transfer object for incoming post /analyze requests and will carry the ticker symbool from frontend
@Data
public class AnalyzeRequestDTO {
    // Ticker must be present, uppercase letters only, max 5 characters no need to check for min since notblank deals iwth that
    @NotBlank(message ="Ticker symbol is required")
    @Size(max = 5,message="Ticker symbol cannot exceed 5 characters")
    @Pattern(regexp ="[A-Z]+", message="Ticker must contain upprrcase letters only")
    private String ticker;

}