package com.example.travelplus.home;

import java.util.List;

public class HomeResponse {
    public int resultCode;
    public String resultMessage;
    public List<homeData> data;

    public static class homeData{
        public boolean isFirst;
        public List<courseHome> course;
    }
    public static class courseHome{
        public int courseId;
        public String title;
        public String area;
        public String startDate;
        public String endDate;
        public String meansTp;
    }
}