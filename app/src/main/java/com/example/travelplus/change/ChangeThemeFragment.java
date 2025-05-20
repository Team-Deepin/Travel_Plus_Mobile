package com.example.travelplus.change;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;
import com.example.travelplus.onboarding.OnboardingResponse;
import com.google.android.material.checkbox.MaterialCheckBox;

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

public class ChangeThemeFragment extends Fragment {
    MaterialCheckBox cityTour, activityTour, emotionTour, shoppingTour, healingTour,
            historyTour, foodTour, natureTour, experienceTour, festivalTour, parkTour;
    ImageView changeBtn;
    ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_theme, container, false);
        apiService = RetrofitClient.getApiInstance(requireContext()).create(ApiService.class);
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
            ChangeThemeRequest changeThemeRequest = new ChangeThemeRequest(selectedTypes);
            Call<ChangeThemeResponse> call = apiService.change(changeThemeRequest);
            call.enqueue(new Callback<ChangeThemeResponse>() {
                @Override
                public void onResponse(Call<ChangeThemeResponse> call, Response<ChangeThemeResponse> response) {
                    Log.d("change theme", String.valueOf(selectedTypes));
                    Log.d("change theme", "응답 코드: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        ChangeThemeResponse res = response.body();
                        Log.d("change theme",res.resultMessage);
                        if (res.resultCode == 200) {
                            Toast.makeText(getContext(), "여행 취향이 변경 되었습니다", Toast.LENGTH_SHORT).show();
                            if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        }
                    }else {
                        if (!response.isSuccessful()){
                            Log.d("change theme", "isSuccessful 실패");
                        }else {
                            Log.d("change theme", "body 실패");
                        }

                    }
                }

                @Override
                public void onFailure(Call<ChangeThemeResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        });
        return view;
    }
}
