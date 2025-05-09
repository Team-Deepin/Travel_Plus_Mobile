package com.example.travelplus.survey;

import com.example.travelplus.recommend.AIRecommendResponse;

import java.io.Serializable;
import java.util.List;

public class SurveyResponse {
    public int resultCode;
    public String resultMessage;
    public List<surveyData> data;

    public static class surveyData implements Serializable {
        public int courseId;
        public List<CourseDetailGroup> courseDetails;
    }

    public static class CourseDetailGroup implements Serializable{
        public String area;
        public List<detailPlace> places;
    }

    public static class detailPlace implements Serializable{
        public String day;
        public String placeName;
        public double placeLat;
        public double placeLon;
        public String placeAddress;
        public int sequence;
        public String placeType;
    }
}
