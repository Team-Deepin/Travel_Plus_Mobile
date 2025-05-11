package com.example.travelplus.change;

import java.util.List;

public class ChangeThemeRequest {
    public List<String> userTripType;

    public ChangeThemeRequest(List<String> userTripType){
        this.userTripType = userTripType;
    }
}
