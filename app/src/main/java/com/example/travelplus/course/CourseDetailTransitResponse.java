package com.example.travelplus.course;

import com.example.travelplus.BaseResponse;

import java.util.List;

public class CourseDetailTransitResponse extends BaseResponse {
    public List<transitData> data;

    public static class transitData{
        public String meansTp;
        public String day;
        public List<transitDetail> transitDetails;
    }
    public static class transitDetail{
        public String from;
        public String to;
        public double fromLat;
        public double fromLon;
        public double toLat;
        public double toLon;
        public List<path> paths;
    }
    public static class path{
        public String mode;
        public String start;
        public String end;
        public String route;
        public int sectionTime;
    }
}
