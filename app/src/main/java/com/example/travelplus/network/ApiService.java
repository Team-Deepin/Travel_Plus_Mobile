package com.example.travelplus.network;

import com.example.travelplus.Login.LoginRequest;
import com.example.travelplus.Login.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}