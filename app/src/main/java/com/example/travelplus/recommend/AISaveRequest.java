package com.example.travelplus.recommend;

import java.util.ArrayList;
import java.util.List;

public class AISaveRequest {
    public SaveData data;

    public AISaveRequest(int courseId, String modelType, List<AIRecommendResponse.CourseDetailGroup> courseDetails) {
        List<List<AIRecommendResponse.detailPlace>> nestedPlaces = new ArrayList<>();
        for (AIRecommendResponse.CourseDetailGroup group : courseDetails) {
            nestedPlaces.add(group.places);
        }
        this.data = new SaveData(courseId, modelType, nestedPlaces);
    }

    public static class SaveData {
        public int courseId;
        public String modelType;
        public List<List<AIRecommendResponse.detailPlace>> courseDetails;

        public SaveData(int courseId, String modelType, List<List<AIRecommendResponse.detailPlace>> courseDetails) {
            this.courseId = courseId;
            this.modelType = modelType;
            this.courseDetails = courseDetails;
        }
    }
}

