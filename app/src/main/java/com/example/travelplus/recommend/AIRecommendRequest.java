package com.example.travelplus.recommend;

public class AIRecommendRequest {
    public String startDate;
    public String endDate;
    public String meansTp;
    public AIRecommendRequest(String startDate, String endDate, String meansTp){
        this.startDate = startDate;
        this.endDate = endDate;
        this.meansTp = meansTp;
    }
}
