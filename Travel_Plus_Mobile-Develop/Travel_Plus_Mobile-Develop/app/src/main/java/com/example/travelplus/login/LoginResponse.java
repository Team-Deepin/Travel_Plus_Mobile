package com.example.travelplus.login;

public class LoginResponse {
    private int resultCode;
    private String resultMessage;
    private Long userId;

    public int getResultCode() {
        return resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public Long getUserId() {
        return userId;
    }
}