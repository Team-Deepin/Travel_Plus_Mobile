package com.example.travelplus;

import java.util.List;

public class MapClass {
    public String day;
    public List<Locations> Location;

    public MapClass(String day, List<Locations> Location) {
        this.day = day;
        this.Location = Location;
    }
    public static class Locations {
        public double fromLat;
        public double fromLon;
        public double toLat;
        public double toLon;

        public Locations(double fromLat, double fromLon, double toLat, double toLon) {
            this.fromLat = fromLat;
            this.fromLon = fromLon;
            this.toLat = toLat;
            this.toLon = toLon;
        }
    }
}