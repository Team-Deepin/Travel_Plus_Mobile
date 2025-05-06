package com.example.travelplus;

import java.util.List;

public class WeatherResponse {
    public List<ForecastItem> list;

    public static class ForecastItem {
        public String dt_txt;
        public Main main;
        public List<Weather> weather;
    }

    public static class Main {
        public float temp;
    }

    public static class Weather {
        public String main;
    }
}

