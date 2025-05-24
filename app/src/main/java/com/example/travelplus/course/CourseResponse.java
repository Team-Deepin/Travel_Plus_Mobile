package com.example.travelplus.course;

import com.example.travelplus.BaseResponse;

import java.util.List;

public class CourseResponse extends BaseResponse {
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
