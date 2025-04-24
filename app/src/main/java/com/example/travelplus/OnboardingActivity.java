package com.example.travelplus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;

public class OnboardingActivity extends AppCompatActivity {
    TextInputEditText age;
    MaterialRadioButton male, female;
    MaterialCheckBox cityTour, activityTour, emotionTour, shoppingTour, healingTour,
            historyTour, foodTour, natureTour, experienceTour, festivalTour, parkTour;
    ImageView onboardingBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
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
            Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "환영합니다! [사용자]님", Toast.LENGTH_SHORT).show();
        });
    }
}
