package com.example.travelplus;

public class CourseDetailList {
    public int resultCode;
    public String resultMessage;
    public String mode;
    public String from;
    public String to;
    public int sectionTime;
    public String route;
    public double distance;
    public String meansTp;

    public CourseDetailList(int resultCode, String resultMessage, String mode, String from, String to, int sectionTime,
                            String route, double distance, String meansTp) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.mode = mode;
        this.from = from;
        this.to = to;
        this.sectionTime = sectionTime;
        this.route = route;
        this.distance = distance;
        this.meansTp = meansTp;
    }
}
