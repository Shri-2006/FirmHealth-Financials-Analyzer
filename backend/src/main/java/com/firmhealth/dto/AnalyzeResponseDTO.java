package com.firmhealth.dto;
import lombok.Data;

//dto for the outgoing response, basically gettign a response back after the post /analsze was completed, and we are sending back to frontend..
@Data
public class AnalyzeResponseDTO{
//ticker that was analyzed, basically the firm initials
    private String ticker;

    //health score from 0 to 100
    private int healthScore;

    //True if firm has signficiant finanical distress, false if its seeming fine for now
    private boolean distressFlag;

    //credit recommendations for humans
    private String creditRecommendation;

    //status of the analysis pipeline
    private String status;

}