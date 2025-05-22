package com.example.travelplus.survey;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyResultFragment extends Fragment {
    String title, meansTp, date, area, person;
    List<String> tripType;
    SurveyResponse.surveyData data;
    ApiService apiService;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            meansTp = getArguments().getString("meansTp");
            area = getArguments().getString("area");
            date = getArguments().getString("date");
            person = getArguments().getString("person");
            tripType = getArguments().getStringArrayList("tripType");
            data = (SurveyResponse.surveyData) getArguments().getSerializable("data");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_survey_result, container, false);
        TextView titleView = view.findViewById(R.id.survey_title);
        LinearLayout surveyList = view.findViewById(R.id.survey_list);
        ImageView surveySelectBtn = view.findViewById(R.id.survey_select_btn);
        apiService = RetrofitClient.getApiInstance(requireContext()).create(ApiService.class);

        titleView.setText(title);
        if (title.isEmpty()){
            titleView.setText(area + " "+date + " "+ meansTp);
        }
        for (SurveyResponse.CourseDetailGroup group : data.courseDetails) {
            LinearLayout courseCard = new LinearLayout(requireContext());
            courseCard.setTag(group.courseIdx);
            courseCard.setOrientation(LinearLayout.VERTICAL);
            courseCard.setBackgroundResource(R.drawable.ai_background);
            LinearLayout.LayoutParams courseParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            courseParams.setMargins(0, 0, 0, 40);
            courseCard.setLayoutParams(courseParams);
            courseCard.setOnClickListener(v -> {
                for (int i = 0; i < surveyList.getChildCount(); i++) {
                    surveyList.getChildAt(i).setSelected(false);
                }
                v.setSelected(true);
            });

            TextView courseTitle = new TextView(requireContext());
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            titleParams.setMargins(40, 30, 0, 20);
            courseTitle.setLayoutParams(titleParams);
            courseTitle.setText(area + " (" + date + ") " + meansTp);
            courseTitle.setTextSize(20);
            courseTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_text));
            courseTitle.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.bmeuljirottf));
            courseCard.addView(courseTitle);

            // 날짜별로 그룹화
            Map<String, List<SurveyResponse.detailPlace>> dayGrouped = new LinkedHashMap<>();
            for (SurveyResponse.detailPlace place : group.places) {
                dayGrouped.computeIfAbsent(place.day, k -> new ArrayList<>()).add(place);
            }

            for (Map.Entry<String, List<SurveyResponse.detailPlace>> entry : dayGrouped.entrySet()) {
                String day = entry.getKey();
                List<SurveyResponse.detailPlace> places = entry.getValue();

                TextView dayText = new TextView(requireContext());
                LinearLayout.LayoutParams dayParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                dayParams.setMargins(30, 20, 0, 10);
                dayText.setLayoutParams(dayParams);
                dayText.setText("📅 " + day);
                dayText.setTextSize(18);
                dayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_text));
                dayText.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.bmeuljirottf));
                courseCard.addView(dayText);

                for (SurveyResponse.detailPlace place : places) {
                    View placeCard = inflater.inflate(R.layout.fragment_survey_result_list, null, false);
                    TextView placeText = placeCard.findViewById(R.id.survey_result_place);
                    placeText.setText(place.placeName);
                    courseCard.addView(placeCard);
                }
            }

            surveyList.addView(courseCard);
        }

        surveySelectBtn.setOnClickListener(v -> {
            boolean hasSelection = false;
            int selectedCourseId = -1;

            for (int i = 0; i < surveyList.getChildCount(); i++) {
                View child = surveyList.getChildAt(i);
                if (child instanceof LinearLayout && child.isSelected()) {
                    hasSelection = true;
                    selectedCourseId = (int) child.getTag();
                    break;
                }
            }

            if (!hasSelection) {
                Toast.makeText(requireContext(), "코스를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            SurveyResponse.CourseDetailGroup foundData = null;
            for (SurveyResponse.CourseDetailGroup d : data.courseDetails) {
                if (d.courseIdx == selectedCourseId) {
                    foundData = d;
                    break;
                }
            }

            if (foundData != null) {
                SurveyResponse.CourseDetailGroup selectedData = foundData;
                SurveySaveRequest surveySaveRequest = new SurveySaveRequest(
                        data.model_name,
                        data.modelType,
                        area,
                        meansTp,
                        person,
                        (String) titleView.getText(),
                        tripType,
                        new ArrayList<SurveyResponse.CourseDetailGroup>() {{
                            add(selectedData);
                        }}
                );

                Call<SurveySaveResponse> call = apiService.surveySave(surveySaveRequest);
                call.enqueue(new Callback<SurveySaveResponse>() {
                    @Override
                    public void onResponse(Call<SurveySaveResponse> call, Response<SurveySaveResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            SurveySaveResponse res = response.body();
                            Log.d("surveySave", res.resultMessage);
                            if (res.resultCode == 200) {
                                Log.d("surveySave", "성공");
                                Bundle result = new Bundle();
                                result.putBoolean("refresh_need", true);
                                getParentFragmentManager().setFragmentResult("refresh_course", result);

                                requireActivity().getSupportFragmentManager()
                                        .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                        } else {
                            Log.d("surveySave", "실패");
                        }
                    }

                    @Override
                    public void onFailure(Call<SurveySaveResponse> call, Throwable t) {
                        Log.e("surveySave", "네트워크 오류", t);
                        t.printStackTrace();
                    }
                });
            }
        });

        return view;
    }
}
