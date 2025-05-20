package com.example.travelplus.recommend;

public class AIRecommendRequest {
    public String start_date;
    public String end_date;
    public String means_tp;
    public AIRecommendRequest(String start_date, String end_date, String means_tp){
        this.start_date = start_date;
        this.end_date = end_date;
        this.means_tp = means_tp;
    }
}
