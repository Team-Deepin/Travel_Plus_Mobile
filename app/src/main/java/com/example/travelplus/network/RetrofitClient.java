package com.example.travelplus.network;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit loginRetrofit;
    private static Retrofit apiRetrofit;
    private static final String BASE_URL = "http://182.230.40.124:8080/";

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
                        SharedPreferences prefs = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                        String token = prefs.getString("authorization", null);

                        Request request = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + token)
                                .build();
                        return chain.proceed(request);
                    })
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