package com.example.travelplus.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.travelplus.BaseResponse;
import com.example.travelplus.MainActivity;
import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OnboardingActivity extends AppCompatActivity {
    TextInputEditText age;
    TextView inputText;
    MaterialRadioButton male, female;
    MaterialCheckBox cityTour, activityTour, emotionTour, shoppingTour, healingTour,
            historyTour, foodTour, natureTour, experienceTour, festivalTour, parkTour;
    CardView onboardingBtn;
    ApiService apiService;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getApiInstance(this).create(ApiService.class);
        setContentView(R.layout.activity_onboarding);
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
        inputText = findViewById(R.id.onboarding_input_text);
        onboardingBtn.setEnabled(false);
        Runnable setButton = new Runnable() {
            @Override
            public void run() {
                String birth = age.getText().toString().trim();
                boolean isGenderChecked = male.isChecked() || female.isChecked();
                boolean isTypeChecked = cityTour.isChecked() || activityTour.isChecked() ||
                        emotionTour.isChecked() || shoppingTour.isChecked() || healingTour.isChecked()
                        || historyTour.isChecked() || foodTour.isChecked() || natureTour.isChecked() ||
                        experienceTour.isChecked() || festivalTour.isChecked() || parkTour.isChecked();

                if(isGenderChecked && isTypeChecked && birth.length() == 8){
//                    onboardingBtn.setImageResource(R.drawable.input_activated);
                    onboardingBtn.setCardBackgroundColor(ContextCompat.getColor(OnboardingActivity.this,R.color.color_button1));
                    inputText.setTextColor(ContextCompat.getColor(OnboardingActivity.this, R.color.color_background));
                    onboardingBtn.setEnabled(true);
                }else{
//                    onboardingBtn.setImageResource(R.drawable.input_deactivated);
                    onboardingBtn.setCardBackgroundColor(ContextCompat.getColor(OnboardingActivity.this,R.color.gray));
                    inputText.setTextColor(ContextCompat.getColor(OnboardingActivity.this, R.color.black));
                    onboardingBtn.setEnabled(false);
                }
            }
        };
        age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setButton.run();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

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
            String formattedDate = null;
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
            SimpleDateFormat outputFormat  = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            try {
                Date birthDate = inputFormat.parse(birth);
                formattedDate = outputFormat.format(birthDate);
            }catch (ParseException e){
                e.printStackTrace();
            }
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
            OnboardingRequest request = new OnboardingRequest(gender, formattedDate, selectedTypes);
            Call<BaseResponse> call = apiService.onboarding(request);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    Log.d("Onboarding", "응답 코드: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        BaseResponse res = response.body();
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
                        try {
                            Log.e("Onboarding", "Response error: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    runOnUiThread(() -> Toast.makeText(OnboardingActivity.this, "입력 실패", Toast.LENGTH_SHORT).show());
                    t.printStackTrace();
                }
            });
        });
    }
}
