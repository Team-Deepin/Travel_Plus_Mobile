package com.example.travelplus.survey;

import java.util.ArrayList;
import java.util.List;

public class SurveySaveRequest {
    public SaveData data;

    public SurveySaveRequest(int courseId, String modelType, List<SurveyResponse.CourseDetailGroup> courseDetails) {
        List<List<SurveyResponse.detailPlace>> nestedPlaces = new ArrayList<>();
        for (SurveyResponse.CourseDetailGroup group : courseDetails) {
            nestedPlaces.add(group.places);
        }
        this.data = new SaveData(courseId, modelType, nestedPlaces);
    }

    public static class SaveData {
        public int courseId;
        public String modelType;
        public List<List<SurveyResponse.detailPlace>> courseDetails;

        public SaveData(int courseId, String modelType, List<List<SurveyResponse.detailPlace>> courseDetails) {
            this.courseId = courseId;
            this.modelType = modelType;
            this.courseDetails = courseDetails;
        }
    }
}

