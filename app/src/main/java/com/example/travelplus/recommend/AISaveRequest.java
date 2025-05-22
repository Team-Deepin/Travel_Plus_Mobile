package com.example.travelplus.recommend;

import com.example.travelplus.survey.SurveyResponse;

import java.util.ArrayList;
import java.util.List;

public class AISaveRequest {
    public String modelName;
    public String modelType;
    public String area;
    public String meansTp;
    public String title;
    public List<String> tripType;
    public List<AIRecommendResponse.CourseDetailGroup> courseDetails;

    public AISaveRequest(String modelName, String modelType, String area, String meansTp,
                         String title, List<String> tripType, List<AIRecommendResponse.CourseDetailGroup> courseDetails){
        this.modelName = modelName;
        this.modelType = modelType;
        this.area = area;
        this.meansTp = meansTp;
        this.title = title;
        this.tripType = tripType;
        this.courseDetails = courseDetails;

    }
}

