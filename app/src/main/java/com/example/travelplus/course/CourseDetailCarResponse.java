package com.example.travelplus.course;

import java.util.List;

public class CourseDetailCarResponse {
    public int resultCode;
    public String resultMessage;
    public List<carData> data;

    public static class carData{
        public String meansTp;
        public String day;
        public List<route> routes;
    }
    public static class route{
        public String start;
        public String end;
        public int sectionTime;
        public double distance;
        public double startLat;
        public double startLon;
        public double endLat;
        public double endLon;
    }
}
