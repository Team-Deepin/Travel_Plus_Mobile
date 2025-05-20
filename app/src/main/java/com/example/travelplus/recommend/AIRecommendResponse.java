package com.example.travelplus.recommend;

import java.io.Serializable;
import java.util.List;

public class AIRecommendResponse {
    public int resultCode;
    public String resultMessage;
    public List<AIRecommendData> data;
    public static class AIRecommendData implements Serializable {
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

