package com.example.travelplus.survey;

import java.util.List;

public class SurveySaveRequest {
    public String modelName;
    public String modelType;
    public String area;
    public String meansTp;
    public String person;
    public String title;
    public List<String> tripType;
    public List<SurveyResponse.CourseDetailGroup> courseDetails;

    public SurveySaveRequest(String modelName, String modelType, String area, String meansTp,
                       String person, String title, List<String> tripType, List<SurveyResponse.CourseDetailGroup> courseDetails){
        this.modelName = modelName;
        this.modelType = modelType;
        this.area = area;
        this.meansTp = meansTp;
        this.person = person;
        this.title = title;
        this.tripType = tripType;
        this.courseDetails = courseDetails;

    }
}
