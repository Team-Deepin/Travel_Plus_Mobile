package com.example.travelplus.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelplus.MainActivity;
import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OnboardingActivity extends AppCompatActivity {
    TextInputEditText age;
    MaterialRadioButton male, female;
    MaterialCheckBox cityTour, activityTour, emotionTour, shoppingTour, healingTour,
            historyTour, foodTour, natureTour, experienceTour, festivalTour, parkTour;
    ImageView onboardingBtn;

    ApiService apiService;
    private MockWebServer mockServer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        setupMockServer();
        age = findViewById(R.id.onboarding_birth);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        onboardingBtn = findViewById(R.id.onboarding_input);
        cityTour = findViewById(R.id.city_tour);
        activityTour = findViewById(R.id.activity_tour);
        emotionTour = findViewById(R.id.emotion_tour);
        shoppingTour = findViewById(R.id.shopping_tour);
        healingTour = findViewById(R.id.healing_tour);
        historyTour = findViewById(R.id.history_tour);
        foodTour = findViewById(R.id.food_tour);
        natureTour = findViewById(R.id.nature_tour);
        experienceTour = findViewById(R.id.experience_tour);
        festivalTour = findViewById(R.id.festival_tour);
        parkTour = findViewById(R.id.park_tour);
        onboardingBtn.setEnabled(false);
        Runnable setButton = new Runnable() {
            @Override
            public void run() {
                String birth = age.getText().toString().trim();
                boolean isSexchecked = male.isChecked() || female.isChecked();
                boolean isTypechecked = cityTour.isChecked() || activityTour.isChecked() ||
                        emotionTour.isChecked() || shoppingTour.isChecked() || healingTour.isChecked()
                        || historyTour.isChecked() || foodTour.isChecked() || natureTour.isChecked() ||
                        experienceTour.isChecked() || festivalTour.isChecked() || parkTour.isChecked();

                if(isSexchecked && isTypechecked && !birth.isEmpty()){
                    onboardingBtn.setImageResource(R.drawable.input_activated);
                    onboardingBtn.setEnabled(true);
                }else{
                    onboardingBtn.setImageResource(R.drawable.input_deactivated);
                    onboardingBtn.setEnabled(false);
                }
            }
        };
        male.setOnCheckedChangeListener((buttonView, isChecked)-> setButton.run());
        female.setOnCheckedChangeListener((buttonView, isChecked)-> setButton.run());
        cityTour.setOnCheckedChangeListener((buttonView, isChecked)-> setButton.run());
        activityTour.setOnCheckedChangeListener((buttonView, isChecked)-> setButton.run());
        emotionTour.setOnCheckedChangeListener((buttonView, isChecked)-> setButton.run());
        shoppingTour.setOnCheckedChangeListener((buttonView, isChecked)-> setButton.run());
        historyTour.setOnCheckedChangeListener((buttonView, isChecked)-> setButton.run());
        healingTour.setOnCheckedChangeListener((buttonView, isChecked)-> setButton.run());
        foodTour.setOnCheckedChangeListener((buttonView, isChecked)-> setButton.run());
        natureTour.setOnCheckedChangeListener((buttonView, isChecked)-> setButton.run());
        experienceTour.setOnCheckedChangeListener((buttonView, isChecked)-> setButton.run());
        festivalTour.setOnCheckedChangeListener((buttonView, isChecked)-> setButton.run());
        parkTour.setOnCheckedChangeListener((buttonView, isChecked)-> setButton.run());
        onboardingBtn.setOnClickListener(view -> {
            String gender = male.isChecked() ? "male" : (female.isChecked() ? "female" : "");
            String birth = age.getText().toString().trim();
            List<String> selectedTypes = new ArrayList<>();
            if (cityTour.isChecked()) selectedTypes.add("도시관광/건축물");
            if (activityTour.isChecked()) selectedTypes.add("레포츠/야외활동");
            if (emotionTour.isChecked()) selectedTypes.add("문화시설");
            if (shoppingTour.isChecked()) selectedTypes.add("쇼핑");
            if (healingTour.isChecked()) selectedTypes.add("온천/헬스케어");
            if (historyTour.isChecked()) selectedTypes.add("역사/문화유산");
            if (foodTour.isChecked()) selectedTypes.add("음식/카페");
            if (natureTour.isChecked()) selectedTypes.add("자연관광");
            if (experienceTour.isChecked()) selectedTypes.add("체험관광");
            if (festivalTour.isChecked()) selectedTypes.add("축제/공연/이벤트");
            if (parkTour.isChecked()) selectedTypes.add("테마파크/공원");
            OnboardingRequest request = new OnboardingRequest(gender, birth, selectedTypes);
            Call<OnboardingResponse> call = apiService.onboarding(request);
            call.enqueue(new Callback<OnboardingResponse>() {
                @Override
                public void onResponse(Call<OnboardingResponse> call, Response<OnboardingResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        OnboardingResponse res = response.body();
                        Log.d("Onboarding",res.resultMessage);
                        if (res.resultCode == 200) {
                            Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            runOnUiThread(() -> Toast.makeText(OnboardingActivity.this, "입력 성공", Toast.LENGTH_SHORT).show());
                        }else{
                            runOnUiThread(() -> Toast.makeText(OnboardingActivity.this, "입력 실패", Toast.LENGTH_SHORT).show());
                            Log.d("Onboarding",String.valueOf(res.resultCode));
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(OnboardingActivity.this, "입력 실패", Toast.LENGTH_SHORT).show());
                        Log.d("Onboarding","온보딩 실패");
                    }
                }

                @Override
                public void onFailure(Call<OnboardingResponse> call, Throwable t) {
                    runOnUiThread(() -> Toast.makeText(OnboardingActivity.this, "입력 실패", Toast.LENGTH_SHORT).show());
                    Log.d("Onboarding","서버 연결 실패");
                }
            });
        });
    }
    private void setupMockServer() {
        new Thread(() -> {
            try {
                mockServer = new MockWebServer();
                mockServer.enqueue(new MockResponse()
                        .setResponseCode(200)
                        .setBody("{\"resultCode\":200,\"resultMessage\":\"Success\"}"));
                mockServer.start();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(mockServer.url("/"))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                apiService = retrofit.create(ApiService.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
