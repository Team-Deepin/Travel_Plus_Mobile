package com.example.travelplus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelplus.home.HomeResponse;
import com.example.travelplus.login.LoginActivity;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading);

        SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String accessToken = prefs.getString("authorization", null);

        if (accessToken != null && !accessToken.isEmpty()) {
            ApiService apiService = RetrofitClient.getApiInstance(this).create(ApiService.class);
            apiService.home().enqueue(new Callback<HomeResponse>() {
                @Override
                public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                    if (response.isSuccessful()) {
                        startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                    } else {
                        goToLogin();
                    }
                    finish();
                }

                @Override
                public void onFailure(Call<HomeResponse> call, Throwable t) {
                    goToLogin();
                    finish();
                }
            });
        } else {
            goToLogin();
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
