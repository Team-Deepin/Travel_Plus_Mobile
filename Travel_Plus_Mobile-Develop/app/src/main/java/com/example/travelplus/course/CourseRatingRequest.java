package com.example.travelplus.course;

public class CourseRatingRequest {
    public long userId;
    public int courseId;
    public double score;

    public CourseRatingRequest(long userId, int courseId, double score){
        this.userId = userId;
        this.courseId = courseId;
        this.score = score;
    };
}
