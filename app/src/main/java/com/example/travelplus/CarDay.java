package com.example.travelplus;

import java.util.List;

public class CarDay {
    public String day;
    public List<CarLocation> carLocations;

    public CarDay(String day, List<CarLocation> carLocations) {
        this.day = day;
        this.carLocations = carLocations;
    }
}