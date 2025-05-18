package com.example.travelplus.course;

public class CourseRatingRequest {
    public int courseId;
    public double score;

    public CourseRatingRequest(int courseId, double score){
        this.courseId = courseId;
        this.score = score;
    };
}
