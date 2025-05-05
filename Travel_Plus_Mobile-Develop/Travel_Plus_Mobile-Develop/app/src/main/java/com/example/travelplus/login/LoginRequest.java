package com.example.travelplus.login;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("email")  // JSON 필드를 email로 보냄
    public String email;

    public String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}