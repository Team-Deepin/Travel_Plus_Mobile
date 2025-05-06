package com.example.travelplus;

import java.util.Date;

public class WeatherList {
    public String location;
    public Date date;
    public float temperature;

    public WeatherList(String location, Date date, float temperature) {
        this.location = location;
        this.date = date;
        this.temperature = temperature;
    }
}
