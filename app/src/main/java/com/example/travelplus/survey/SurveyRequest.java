package com.example.travelplus.survey;

import java.util.List;

public class SurveyRequest {
    public String title;
    public String area;
    public String meansTp;
    public String person;
    public String startDate;
    public String endDate;
    public List<String> tripType;

    public SurveyRequest(String title, String area, String meansTp, String person,
                         String startDate, String endDate, List<String> tripType){
        this.title = title;
        this.area = area;
        this.meansTp = meansTp;
        this.person = person;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tripType = tripType;
    }
}
