package com.example.travelplus.course;

import java.util.List;

public class CourseDetailCarResponse {
    public int resultCode;
    public String resultMessage;
    public List<carData> data;

    public static class carData{
        public String day;
        public List<carDetail> carDetails;
    }
    public static class carDetail{
        public String start;
        public String end;
        public int sectionTime;
        public double distance;
        public String meansTp;
    }
}
