package com.example.travelplus.course;

import java.util.List;

public class CourseHistoryResponse {
    public int resultCode;
    public String resultMessage;
    public List<Course> data;

    public static class Course{
        public int courseId;
        public String title;
        public String area;
        public String tripType;
        public String startDate;
        public String endDate;
        public String meansTp;
    };
}
