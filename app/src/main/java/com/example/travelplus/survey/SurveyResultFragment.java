package com.example.travelplus.survey;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SurveyResultFragment extends Fragment {
    String title, transit, date, authorization;
    List<SurveyResponse.surveyData> data;
    ApiService apiService;
    private MockWebServer mockServer;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            transit = getArguments().getString("transit");
            date = getArguments().getString("date");
            data = (List<SurveyResponse.surveyData>) getArguments().getSerializable("data");
        }
        SharedPreferences prefs = requireActivity().getSharedPreferences("userPrefs", MODE_PRIVATE);
        authorization = prefs.getString("authorization", null);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_survey_result, container, false);
        TextView titleView = view.findViewById(R.id.survey_title);
        LinearLayout surveyList = view.findViewById(R.id.survey_list);
        ImageView surveySelectBtn = view.findViewById(R.id.survey_select_btn);
        setupMockServer();

        titleView.setText(title);
        if (title.isEmpty()){
            titleView.setText(data.get(0).courseDetails.get(0).area + " "+date + " "+ transit);
        }
        for (SurveyResponse.surveyData courseData : data) {
            LinearLayout courseCard = new LinearLayout(requireContext());
            courseCard.setTag(courseData.courseId);
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
            courseTitle.setText(courseData.courseDetails.get(0).area + " (" + date + ") " + transit);
            courseTitle.setTextSize(20);
            courseTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_text));
            courseTitle.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.bmeuljirottf));
            courseCard.addView(courseTitle);

            for (SurveyResponse.CourseDetailGroup group : courseData.courseDetails) {
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
                        View placeCard = inflater.inflate(R.layout.fragment_survey_result_list, courseCard, false);
                        TextView placeText = placeCard.findViewById(R.id.survey_result_place);
                        placeText.setText(place.placeName);
                        courseCard.addView(placeCard);
                    }
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

            // 선택된 데이터 찾기
            SurveyResponse.surveyData selectedData = null;
            for (SurveyResponse.surveyData d : data) {
                if (d.courseId == selectedCourseId) {
                    selectedData = d;
                    break;
                }
            }

            if (selectedData != null) {
                SurveySaveRequest surveySaveRequest = new SurveySaveRequest(
                        selectedData.courseId,
                        "콘텐츠기반",
                        selectedData.courseDetails
                );

                Call<SurveySaveResponse> call = apiService.surveySave(authorization, surveySaveRequest);
                call.enqueue(new Callback<SurveySaveResponse>() {
                    @Override
                    public void onResponse(Call<SurveySaveResponse> call, Response<SurveySaveResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            SurveySaveResponse res = response.body();
                            Log.d("surveySave", res.resultMessage);
                            if (res.resultCode == 200) {
                                Log.d("surveySave", "성공");
                                requireActivity().getSupportFragmentManager()
                                        .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                        } else {
                            Log.d("surveySave", "실패");
                        }
                    }

                    @Override
                    public void onFailure(Call<SurveySaveResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

        return view;
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
//            apiService = RetrofitClient.getInstance().create(ApiService.class);
        }).start();
    }
}
