package com.example.travelplus.change;

import java.util.List;

public class ChangeThemeRequest {
    public int userId;
    public List<String> userTripType;

    public ChangeThemeRequest(int userId, List<String> userTripType){
        this.userId = userId;
        this.userTripType = userTripType;
    }
}
