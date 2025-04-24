package com.example.travelplus.network;

import com.example.travelplus.IsFirstResponse;
import com.example.travelplus.Login.LoginRequest;
import com.example.travelplus.Login.LoginResponse;
import com.example.travelplus.Login.LogoutResponse;
import com.example.travelplus.register.RegisterRequest;
import com.example.travelplus.register.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);
    @GET("/auth/logout")
    Call<LogoutResponse> logout();
    @GET("/home")
    Call<IsFirstResponse> getIsFirst();

}