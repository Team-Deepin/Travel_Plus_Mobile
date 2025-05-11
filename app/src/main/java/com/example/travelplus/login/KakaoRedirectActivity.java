package com.example.travelplus.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelplus.MainActivity;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KakaoRedirectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleRedirect(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleRedirect(intent);
    }

    private void handleRedirect(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && uri.getQueryParameter("code") != null) {
            String authCode = uri.getQueryParameter("code");

            // 이제 서버로 이 authCode를 전송해서 로그인 처리
            sendAuthCodeToServer(authCode);
        } else {
            Log.e("KakaoRedirect", "인가 코드 없음");
        }
    }

    private void sendAuthCodeToServer(String authCode) {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);

        Call<KakaoResponse> call = api.kakao(authCode);
        call.enqueue(new Callback<KakaoResponse>() {
            @Override
            public void onResponse(Call<KakaoResponse> call, Response<KakaoResponse> response) {
                if (response.isSuccessful()) {
                    // 로그인 성공 → 메인 이동
                    String authorization = response.headers().get("Authorization");
                    if (authorization != null) {
                        SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("authorization", authorization);
                        editor.apply();
                        Log.d("Login", "저장 완료: " + authorization);
                    }
                    startActivity(new Intent(KakaoRedirectActivity.this, MainActivity.class));
                    finish();
                } else {
                    Log.e("KakaoRedirect", "서버 로그인 실패");
                }
            }

            @Override
            public void onFailure(Call<KakaoResponse> call, Throwable t) {
                Log.e("KakaoRedirect", "네트워크 오류", t);
            }
        });
    }
}
