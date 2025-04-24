package com.example.travelplus.register;

public class RegisterRequest {
    public String username;
    public String password;
    public String name;

    public RegisterRequest(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }
}