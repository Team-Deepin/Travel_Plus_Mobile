package com.example.travelplus.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit loginRetrofit;
    private static Retrofit apiRetrofit;
    private static final String BASE_URL = "http://ceprj.gachon.ac.kr:60008/";

    public static Retrofit getLoginInstance() {
        if (loginRetrofit == null) {
            loginRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return loginRetrofit;
    }

    public static Retrofit getApiInstance(Context context) {
        if (apiRetrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder();

                        SharedPreferences prefs = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                        String token = prefs.getString("authorization", null);

                        if (token != null) {
                            builder.addHeader("Authorization", token);
                        }

                        return chain.proceed(builder.build());
                    })
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            apiRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return apiRetrofit;
    }
}