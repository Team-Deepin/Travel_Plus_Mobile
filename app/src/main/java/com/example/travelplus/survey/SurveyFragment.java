package com.example.travelplus.survey;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SurveyFragment extends Fragment {
    boolean placeFlag = true, dateFlag = true, transportFlag = true,
            peopleFlag = true, themeFlag = true;
    boolean placeCheck = false, dateCheck = false, transportCheck = false,
            peopleCheck = false, themeCheck = false;
    String title, area, meansTp, startDate, endDate, person, authorization, personSend;
    List<String> tripType = new ArrayList<>();
    MaterialCardView surveyBtn;
    ApiService apiService;
    ShimmerFrameLayout surveySkeleton;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
        }
        SharedPreferences prefs = requireActivity().getSharedPreferences("userPrefs", MODE_PRIVATE);
        authorization = prefs.getString("authorization", null);
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_survey, container, false);
        apiService = RetrofitClient.getApiInstance(requireContext()).create(ApiService.class);
        surveySkeleton = view.findViewById(R.id.survey_skeleton);

        // 장소 요소
        CardView cardPlace = view.findViewById(R.id.card_place);
        ImageView placeDown = view.findViewById(R.id.survey_place_down);
        CardView placeSelect = view.findViewById(R.id.survey_place_select);
        TextView placeSelectText = view.findViewById(R.id.survey_place_select_text);
        CardView placeSeoul = view.findViewById(R.id.place_seoul);
        CardView placeGyeonggi = view.findViewById(R.id.place_gyeonggi);
        CardView placeGangwon = view.findViewById(R.id.place_gangwon);
        CardView placeChungbuk = view.findViewById(R.id.place_chungbuk);
        CardView placeChungnam = view.findViewById(R.id.place_chungnam);
        CardView placeJeonbuk = view.findViewById(R.id.place_jeonbuk);
        CardView placeJeonnam = view.findViewById(R.id.place_jeonnam);
        CardView placeGyeongbuk = view.findViewById(R.id.place_gyeongbuk);
        CardView placeGyeongnam = view.findViewById(R.id.place_gyeongnam);
        CardView placeJeju = view.findViewById(R.id.place_jeju);

        // 날짜 요소
        CardView cardDate = view.findViewById(R.id.card_trip_date);
        ImageView dateDown = view.findViewById(R.id.survey_date_down);
        CardView dateSelect = view.findViewById(R.id.survey_date_select);
        TextView dateSelectText = view.findViewById(R.id.survey_date_select_text);
        CalendarView dateCalender = view.findViewById(R.id.survey_date);
        CardView dateCheckBtn = view.findViewById(R.id.survey_calendar_check_btn);

        // 이동수단 요소
        CardView cardTransport = view.findViewById(R.id.card_transport);
        ImageView transportDown = view.findViewById(R.id.survey_transport_down);
        CardView transportSelect = view.findViewById(R.id.survey_transport_select);
        TextView transportSelectText = view.findViewById(R.id.survey_transport_select_text);
        CardView transportCar = view.findViewById(R.id.survey_car);
        CardView transportTransit = view.findViewById(R.id.survey_transit);

        // 인원수 요소
        CardView cardPeople = view.findViewById(R.id.card_people);
        ImageView peopleDown = view.findViewById(R.id.survey_people_down);
        CardView peopleSelect = view.findViewById(R.id.survey_people_select);
        TextView peopleSelectText = view.findViewById(R.id.survey_people_select_text);
        CardView people2Family = view.findViewById(R.id.people_2_family);
        CardView people2 = view.findViewById(R.id.people_2);
        CardView people3Gen = view.findViewById(R.id.people_3gen);
        CardView peopleOver3 = view.findViewById(R.id.people_over3);
        CardView people3 = view.findViewById(R.id.people_3);
        CardView peopleSolo = view.findViewById(R.id.people_solo);
        CardView peopleParents = view.findViewById(R.id.people_parents);
        CardView peopleKids = view.findViewById(R.id.people_kids);

        // 여행 테마 요소
        CardView cardTheme = view.findViewById(R.id.card_theme);
        ImageView themeDown = view.findViewById(R.id.survey_theme_down);
        ConstraintLayout cityContainer = view.findViewById(R.id.survey_city_container);
        MaterialCheckBox cityTour = view.findViewById(R.id.survey_city_tour);
        ConstraintLayout activityContainer = view.findViewById(R.id.survey_activity_container);
        MaterialCheckBox activityTour = view.findViewById(R.id.survey_activity_tour);
        ConstraintLayout emotionContainer = view.findViewById(R.id.survey_emotion_container);
        MaterialCheckBox emotionTour = view.findViewById(R.id.survey_emotion_tour);
        ConstraintLayout shoppingContainer = view.findViewById(R.id.survey_shopping_container);
        MaterialCheckBox shoppingTour = view.findViewById(R.id.survey_shopping_tour);
        ConstraintLayout historyContainer = view.findViewById(R.id.survey_history_container);
        MaterialCheckBox historyTour = view.findViewById(R.id.survey_history_tour);
        ConstraintLayout healingContainer = view.findViewById(R.id.survey_healing_container);
        MaterialCheckBox healingTour = view.findViewById(R.id.survey_healing_tour);
        ConstraintLayout foodContainer = view.findViewById(R.id.survey_food_container);
        MaterialCheckBox foodTour = view.findViewById(R.id.survey_food_tour);
        ConstraintLayout natureContainer = view.findViewById(R.id.survey_nature_container);
        MaterialCheckBox natureTour = view.findViewById(R.id.survey_nature_tour);
        ConstraintLayout experienceContainer = view.findViewById(R.id.survey_experience_container);
        MaterialCheckBox experienceTour = view.findViewById(R.id.survey_experience_tour);
        ConstraintLayout festivalContainer = view.findViewById(R.id.survey_festival_container);
        MaterialCheckBox festivalTour = view.findViewById(R.id.survey_festival_tour);
        ConstraintLayout parkContainer = view.findViewById(R.id.survey_park_container);
        MaterialCheckBox parkTour = view.findViewById(R.id.survey_park_tour);
        View viewContainer = view.findViewById(R.id.survey_view_container);

        surveyBtn = view.findViewById(R.id.survey_btn);
        surveyBtn.setEnabled(false);

        // 장소 입력
        placeDown.setOnClickListener(view1 -> {
            if(placeFlag){
                int newHeightPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        250,
                        getResources().getDisplayMetrics()
                );
                ViewGroup.LayoutParams params = cardPlace.getLayoutParams();
                params.height = newHeightPx;
                cardPlace.setLayoutParams(params);
                int color = ContextCompat.getColor(requireContext(), R.color.point);
                cardPlace.setCardBackgroundColor(color);
                placeSeoul.setVisibility(VISIBLE);
                placeGyeonggi.setVisibility(VISIBLE);
                placeGangwon.setVisibility(VISIBLE);
                placeChungbuk.setVisibility(VISIBLE);
                placeChungnam.setVisibility(VISIBLE);
                placeJeonbuk.setVisibility(VISIBLE);
                placeJeonnam.setVisibility(VISIBLE);
                placeGyeongbuk.setVisibility(VISIBLE);
                placeGyeongnam.setVisibility(VISIBLE);
                placeJeju.setVisibility(VISIBLE);
                placeFlag = false;
            }else {
                int newHeightPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        50,
                        getResources().getDisplayMetrics()
                );
                ViewGroup.LayoutParams params = cardPlace.getLayoutParams();
                params.height = newHeightPx;
                cardPlace.setLayoutParams(params);
                int color = ContextCompat.getColor(requireContext(), R.color.login_button);
                cardPlace.setCardBackgroundColor(color);
                placeSeoul.setVisibility(GONE);
                placeGyeonggi.setVisibility(GONE);
                placeGangwon.setVisibility(GONE);
                placeChungbuk.setVisibility(GONE);
                placeChungnam.setVisibility(GONE);
                placeJeonbuk.setVisibility(GONE);
                placeJeonnam.setVisibility(GONE);
                placeGyeongbuk.setVisibility(GONE);
                placeGyeongnam.setVisibility(GONE);
                placeJeju.setVisibility(GONE);
                placeFlag = true;
            }
        });

        placeSeoul.setOnClickListener(view1 -> {
            area = "서울";
            placeCheck = true;
            setText(area, placeSelectText, placeSelect);
        });
        placeGyeonggi.setOnClickListener(view1 -> {
            area = "경기";
            placeCheck = true;
            setText(area, placeSelectText, placeSelect);
        });
        placeGangwon.setOnClickListener(view1 -> {
            area = "강원";
            placeCheck = true;
            setText(area, placeSelectText, placeSelect);
        });
        placeChungbuk.setOnClickListener(view1 -> {
            area = "충북";
            placeCheck = true;
            setText(area, placeSelectText, placeSelect);
        });
        placeChungnam.setOnClickListener(view1 -> {
            area = "충남";
            placeCheck = true;
            setText(area, placeSelectText, placeSelect);
        });
        placeJeonbuk.setOnClickListener(view1 -> {
            area = "전북";
            placeCheck = true;
            setText(area, placeSelectText, placeSelect);
        });
        placeJeonnam.setOnClickListener(view1 -> {
            area = "전남";
            placeCheck = true;
            setText(area, placeSelectText, placeSelect);
        });
        placeGyeongbuk.setOnClickListener(view1 -> {
            area = "경북";
            placeCheck = true;
            setText(area, placeSelectText, placeSelect);
        });
        placeGyeongnam.setOnClickListener(view1 -> {
            area = "경남";
            placeCheck = true;
            setText(area, placeSelectText, placeSelect);
        });
        placeJeju.setOnClickListener(view1 -> {
            area = "제주";
            placeCheck = true;
            setText(area, placeSelectText, placeSelect);
        });

        // 날짜 입력
        dateDown.setOnClickListener(view1 -> {
            if(dateFlag){
                int newHeightPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        465,
                        getResources().getDisplayMetrics()
                );
                ViewGroup.LayoutParams params = cardDate.getLayoutParams();
                params.height = newHeightPx;
                cardDate.setLayoutParams(params);
                int color = ContextCompat.getColor(requireContext(), R.color.point);
                cardDate.setCardBackgroundColor(color);
                dateCalender.setVisibility(VISIBLE);
                dateCheckBtn.setVisibility(VISIBLE);
                dateFlag = false;
            }else {
                int newHeightPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        50,
                        getResources().getDisplayMetrics()
                );
                ViewGroup.LayoutParams params = cardDate.getLayoutParams();
                params.height = newHeightPx;
                cardDate.setLayoutParams(params);
                int color = ContextCompat.getColor(requireContext(), R.color.login_button);
                cardDate.setCardBackgroundColor(color);
                dateCalender.setVisibility(GONE);
                dateCheckBtn.setVisibility(GONE);
                dateFlag = true;
            }
        });
        dateCheckBtn.setOnClickListener(view1 -> {
            List<Calendar> selectedDates = dateCalender.getSelectedDates();
            if (!selectedDates.isEmpty()) {
                Calendar start = selectedDates.get(0);
                Calendar end = selectedDates.get(selectedDates.size() - 1);

                SimpleDateFormat fmt = new SimpleDateFormat("MM/dd", Locale.getDefault());
                String text = fmt.format(start.getTime());
                if (!start.equals(end)) {
                    text += " ~ " + fmt.format(end.getTime());
                }
                dateSelectText.setText(text);
                dateSelect.setVisibility(VISIBLE);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                startDate = dateFormat.format(start.getTime());
                endDate = dateFormat.format(end.getTime());
                dateCheck = true;
                checkInput();
            }
            int newHeightPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    50,
                    getResources().getDisplayMetrics()
            );
            ViewGroup.LayoutParams params = cardDate.getLayoutParams();
            params.height = newHeightPx;
            cardDate.setLayoutParams(params);
            int color = ContextCompat.getColor(requireContext(), R.color.login_button);
            cardDate.setCardBackgroundColor(color);
            dateCalender.setVisibility(GONE);
            dateCheckBtn.setVisibility(GONE);
            dateFlag = true;
        });

        // 이동수단 입력
        transportDown.setOnClickListener(view1 -> {
            if (transportFlag){
                int newHeightPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        130,
                        getResources().getDisplayMetrics()
                );
                ViewGroup.LayoutParams params = cardTransport.getLayoutParams();
                params.height = newHeightPx;
                cardTransport.setLayoutParams(params);
                int color = ContextCompat.getColor(requireContext(), R.color.point);
                cardTransport.setCardBackgroundColor(color);
                transportCar.setVisibility(VISIBLE);
                transportTransit.setVisibility(VISIBLE);
                transportFlag = false;
            }else {
                int newHeightPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        50,
                        getResources().getDisplayMetrics()
                );
                ViewGroup.LayoutParams params = cardTransport.getLayoutParams();
                params.height = newHeightPx;
                cardTransport.setLayoutParams(params);
                int color = ContextCompat.getColor(requireContext(), R.color.login_button);
                cardTransport.setCardBackgroundColor(color);
                transportCar.setVisibility(GONE);
                transportTransit.setVisibility(GONE);
                transportFlag = true;
            }
        });
        transportCar.setOnClickListener(view1 -> {
            meansTp = "자가용";
            transportCheck = true;
            setText(meansTp, transportSelectText, transportSelect);
        });
        transportTransit.setOnClickListener(view1 -> {
            meansTp = "대중교통";
            transportCheck = true;
            setText(meansTp, transportSelectText, transportSelect);
        });

        // 인원수 입력
        peopleDown.setOnClickListener(view1 -> {
            if (peopleFlag){
                int newHeightPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        300,
                        getResources().getDisplayMetrics()
                );
                ViewGroup.LayoutParams params = cardPeople.getLayoutParams();
                params.height = newHeightPx;
                cardPeople.setLayoutParams(params);
                int color = ContextCompat.getColor(requireContext(), R.color.point);
                cardPeople.setCardBackgroundColor(color);
                people2.setVisibility(VISIBLE);
                people2Family.setVisibility(VISIBLE);
                people3.setVisibility(VISIBLE);
                people3Gen.setVisibility(VISIBLE);
                peopleOver3.setVisibility(VISIBLE);
                peopleKids.setVisibility(VISIBLE);
                peopleSolo.setVisibility(VISIBLE);
                peopleParents.setVisibility(VISIBLE);
                peopleFlag = false;
            }else {
                int newHeightPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        50,
                        getResources().getDisplayMetrics()
                );
                ViewGroup.LayoutParams params = cardPeople.getLayoutParams();
                params.height = newHeightPx;
                cardPeople.setLayoutParams(params);
                int color = ContextCompat.getColor(requireContext(), R.color.login_button);
                cardPeople.setCardBackgroundColor(color);
                people2.setVisibility(VISIBLE);
                people2Family.setVisibility(VISIBLE);
                people3.setVisibility(VISIBLE);
                people3Gen.setVisibility(VISIBLE);
                peopleOver3.setVisibility(VISIBLE);
                peopleKids.setVisibility(VISIBLE);
                peopleSolo.setVisibility(VISIBLE);
                peopleParents.setVisibility(VISIBLE);
                peopleFlag = true;
            }
        });

        people2.setOnClickListener(view1 -> {
            person = "가족외 2인 여행";
            personSend = "2인 여행(가족 외)";
            peopleCheck = true;
            setText(person, peopleSelectText, peopleSelect);
        });
        people2Family.setOnClickListener(view1 -> {
            person = "2인 가족 여행";
            personSend = "2인 가족 여행";
            peopleCheck = true;
            setText(person, peopleSelectText, peopleSelect);
        });
        people3Gen.setOnClickListener(view1 -> {
            person = "3대 동반 여행";
            personSend = "3대 동반 여행(친척 포함)";
            peopleCheck = true;
            setText(person, peopleSelectText, peopleSelect);
        });
        peopleOver3.setOnClickListener(view1 -> {
            person = "3인 이상 가족 여행";
            personSend = "3인 이상 가족 여행(친척 포함)";
            peopleCheck = true;
            setText(person, peopleSelectText, peopleSelect);
        });
        people3.setOnClickListener(view1 -> {
            person = "가족외 3인 여행";
            personSend = "3인 이상 여행(가족 외)";
            peopleCheck = true;
            setText(person, peopleSelectText, peopleSelect);
        });
        peopleSolo.setOnClickListener(view1 -> {
            person = "나홀로 여행";
            personSend = "나홀로 여행";
            peopleCheck = true;
            setText(person, peopleSelectText, peopleSelect);
        });
        peopleParents.setOnClickListener(view1 -> {
            person = "부모 동반 여행";
            personSend = "부모 동반 여행";
            peopleCheck = true;
            setText(person, peopleSelectText, peopleSelect);
        });
        peopleKids.setOnClickListener(view1 -> {
            person = "자녀 동반 여행";
            personSend = "자녀 동반 여행";
            peopleCheck = true;
            setText(person, peopleSelectText, peopleSelect);
        });

        // 테마 입력
        themeDown.setOnClickListener(view1 -> {
            if (themeFlag){
                int color = ContextCompat.getColor(requireContext(), R.color.point);
                cardTheme.setCardBackgroundColor(color);
                cityContainer.setVisibility(VISIBLE);
                activityContainer.setVisibility(VISIBLE);
                emotionContainer.setVisibility(VISIBLE);
                foodContainer.setVisibility(VISIBLE);
                shoppingContainer.setVisibility(VISIBLE);
                experienceContainer.setVisibility(VISIBLE);
                festivalContainer.setVisibility(VISIBLE);
                healingContainer.setVisibility(VISIBLE);
                historyContainer.setVisibility(VISIBLE);
                parkContainer.setVisibility(VISIBLE);
                viewContainer.setVisibility(VISIBLE);
                themeFlag = false;
            }else {
                int color = ContextCompat.getColor(requireContext(), R.color.login_button);
                cardTheme.setCardBackgroundColor(color);
                cityContainer.setVisibility(GONE);
                activityContainer.setVisibility(GONE);
                emotionContainer.setVisibility(GONE);
                foodContainer.setVisibility(GONE);
                shoppingContainer.setVisibility(GONE);
                experienceContainer.setVisibility(GONE);
                festivalContainer.setVisibility(GONE);
                healingContainer.setVisibility(GONE);
                historyContainer.setVisibility(GONE);
                parkContainer.setVisibility(GONE);
                viewContainer.setVisibility(GONE);
                themeFlag = true;
            }
        });

        Runnable setButton = new Runnable() {
            @Override
            public void run() {
                themeCheck = cityTour.isChecked() || activityTour.isChecked() ||
                        emotionTour.isChecked() || shoppingTour.isChecked() || healingTour.isChecked()
                        || historyTour.isChecked() || foodTour.isChecked() || natureTour.isChecked() ||
                        experienceTour.isChecked() || festivalTour.isChecked() || parkTour.isChecked();
                checkInput();
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

        surveyBtn.setOnClickListener(view1 -> {
            surveySkeleton.setVisibility(View.VISIBLE);
            surveySkeleton.startShimmer();
            if (cityTour.isChecked()) tripType.add("도시관광/건축물");
            if (activityTour.isChecked()) tripType.add("레포츠/야외활동");
            if (emotionTour.isChecked()) tripType.add("문화시설");
            if (shoppingTour.isChecked()) tripType.add("쇼핑");
            if (healingTour.isChecked()) tripType.add("온천/헬스케어");
            if (historyTour.isChecked()) tripType.add("역사/문화유산");
            if (foodTour.isChecked()) tripType.add("음식/카페");
            if (natureTour.isChecked()) tripType.add("자연관광");
            if (experienceTour.isChecked()) tripType.add("체험관광");
            if (festivalTour.isChecked()) tripType.add("축제/공연/이벤트");
            if (parkTour.isChecked()) tripType.add("테마파크/공원");
            Log.d("survey", startDate+" "+endDate);
            SurveyRequest surveyRequest = new SurveyRequest(area, meansTp, personSend, startDate, endDate, tripType);
            Call<SurveyResponse> call = apiService.survey(surveyRequest);
            call.enqueue(new Callback<SurveyResponse>() {
                @Override
                public void onResponse(Call<SurveyResponse> call, Response<SurveyResponse> response) {
                    surveySkeleton.stopShimmer();
                    surveySkeleton.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("survey", "실패");
                        Log.d("survey", "응답 코드: " + response.code());
                        SurveyResponse res = response.body();
                        Log.d("survey",res.resultMessage);
                        if (res.resultCode == 200) {
                            Bundle bundle = new Bundle();
                            bundle.putString("title",title);
                            bundle.putString("area",area);
                            bundle.putString("date", dateSelectText.getText().toString().trim());
                            bundle.putString("meansTp",meansTp);
                            bundle.putString("person",personSend);
                            bundle.putStringArrayList("tripType",new ArrayList<>(tripType));
                            bundle.putSerializable("data",(Serializable) res.data);

                            SurveyResultFragment resultFragment = new SurveyResultFragment();
                            resultFragment.setArguments(bundle);
                            NestedScrollView surveyScroll = requireView().findViewById(R.id.survey_scroll);
                            ConstraintLayout surveyLayout = requireView().findViewById(R.id.survey_layout);
                            surveyLayout.setVisibility(GONE);
                            surveyScroll.setVisibility(GONE);
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.survey_fragment_container, resultFragment)
                                    .commit();
                        }else {
                            Log.d("survey",res.resultMessage);
                        }
                    }
                }
                @Override
                public void onFailure(Call<SurveyResponse> call, Throwable t) {
                    surveySkeleton.stopShimmer();
                    surveySkeleton.setVisibility(View.GONE);
                    t.printStackTrace();
                }
            });
        });
        return view;
    }
    private void checkInput(){
        boolean enabled = placeCheck && dateCheck && transportCheck && peopleCheck && themeCheck;
        surveyBtn.setEnabled(enabled);
        if (enabled) {
            int color = ContextCompat.getColor(requireContext(), R.color.color_button1);
            surveyBtn.setCardBackgroundColor(color);
        } else {
            int color = ContextCompat.getColor(requireContext(), R.color.survey_deactivate);
            surveyBtn.setCardBackgroundColor(color);
        }
    }
    private void setText(String text, TextView textView, CardView cardView){
        textView.setText(text);
        cardView.setVisibility(VISIBLE);
        checkInput();
    }
}
