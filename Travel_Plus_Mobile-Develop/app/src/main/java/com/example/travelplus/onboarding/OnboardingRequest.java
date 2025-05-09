package com.example.travelplus.onboarding;

import java.util.List;

public class OnboardingRequest {
    public String gender;
    public List<String> userTripType;
    public String birth;

    public OnboardingRequest(String gender, String birth, List<String> userTripType) {
        this.gender = gender;
        this.birth = birth;
        this.userTripType = userTripType;
    }
}
