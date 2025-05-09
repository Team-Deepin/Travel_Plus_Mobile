package com.example.travelplus;

import java.util.List;

public class IsFirstResponse {
    public int resultCode;
    public String resultMessage;
    public List<homeData> data;

    public static class homeData{
        public boolean isFirst;
        public List<courseHome> course;

        public int courseId;
        public String title;
        public String area;
        public String startDate;
        public String endDate;
        public String meansTp;
    }
}