package com.example.travelplus.network;

import com.example.travelplus.IsFirstResponse;
import com.example.travelplus.login.LoginRequest;
import com.example.travelplus.login.LoginResponse;
import com.example.travelplus.login.LogoutResponse;
import com.example.travelplus.onboarding.OnboardingRequest;
import com.example.travelplus.onboarding.OnboardingResponse;
import com.example.travelplus.register.RegisterRequest;
import com.example.travelplus.register.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);
    @POST("/auth/onboarding")
    Call<OnboardingResponse> onboarding(@Body OnboardingRequest request);
    @GET("/auth/logout")
    Call<LogoutResponse> logout();
    @GET("/home")
    Call<IsFirstResponse> getIsFirst();



}