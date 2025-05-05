package com.example.travelplus.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travelplus.R;
import com.google.android.material.checkbox.MaterialCheckBox;

public class ChangeThemeFragment extends Fragment {
    MaterialCheckBox cityTour, activityTour, emotionTour, shoppingTour, healingTour,
            historyTour, foodTour, natureTour, experienceTour, festivalTour, parkTour;
    ImageView changeBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_theme, container, false);
        cityTour = view.findViewById(R.id.change_city_tour);
        activityTour = view.findViewById(R.id.change_activity_tour);
        emotionTour = view.findViewById(R.id.change_emotion_tour);
        shoppingTour = view.findViewById(R.id.change_shopping_tour);
        healingTour = view.findViewById(R.id.change_healing_tour);
        historyTour = view.findViewById(R.id.change_history_tour);
        foodTour = view.findViewById(R.id.change_food_tour);
        natureTour = view.findViewById(R.id.change_nature_tour);
        experienceTour = view.findViewById(R.id.change_experience_tour);
        festivalTour = view.findViewById(R.id.change_festival_tour);
        parkTour = view.findViewById(R.id.change_park_tour);
        changeBtn = view.findViewById(R.id.change_theme_btn);
        changeBtn.setEnabled(false);
        Runnable setButton = new Runnable() {
            @Override
            public void run() {
                boolean isTypechecked = cityTour.isChecked() || activityTour.isChecked() ||
                        emotionTour.isChecked() || shoppingTour.isChecked() || healingTour.isChecked()
                        || historyTour.isChecked() || foodTour.isChecked() || natureTour.isChecked() ||
                        experienceTour.isChecked() || festivalTour.isChecked() || parkTour.isChecked();

                if(isTypechecked){
                    changeBtn.setEnabled(true);
                }else{
                    changeBtn.setEnabled(false);
                }
            }
        };
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

        changeBtn.setOnClickListener(view1 -> {
            // 취향 변경하기
            // 이전 프래그먼트로 돌아가기
            if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
    }
}
