package com.example.travelplus.survey;

import java.util.List;

public class SurveyRequest {
    public String area;
    public String meansTp;
    public String person;
    public String start_date;
    public String end_date;
    public List<String> tripType;

    public SurveyRequest(String area, String meansTp, String person,
                         String start_date, String end_date, List<String> tripType){
        this.area = area;
        this.meansTp = meansTp;
        this.person = person;
        this.start_date = start_date;
        this.end_date = end_date;
        this.tripType = tripType;
    }
}
