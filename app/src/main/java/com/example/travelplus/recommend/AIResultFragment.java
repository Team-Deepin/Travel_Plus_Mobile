package com.example.travelplus.recommend;

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
import com.example.travelplus.course.CourseResponse;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;
import com.example.travelplus.survey.SurveyResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AIResultFragment extends Fragment {
    String title, transit, date;
    AIRecommendResponse.AIRecommendData data;
    ApiService apiService;
    Set<String> tripType = new HashSet<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            transit = getArguments().getString("transit");
            date = getArguments().getString("date");
            data = (AIRecommendResponse.AIRecommendData) getArguments().getSerializable("data");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_recommend_result, container, false);
        TextView titleView = view.findViewById(R.id.ai_title);
        LinearLayout aiList = view.findViewById(R.id.ai_list);
        ImageView aiSelectBtn = view.findViewById(R.id.ai_select_btn);
        apiService = RetrofitClient.getApiInstance(requireContext()).create(ApiService.class);

        titleView.setText(title);
        if (title.isEmpty()){
            titleView.setText(date + " "+ transit);
        }
        for (AIRecommendResponse.CourseDetailGroup group : data.courseDetails) {
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
                for (int i = 0; i < aiList.getChildCount(); i++) {
                    aiList.getChildAt(i).setSelected(false);
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
            courseTitle.setText(group.area + " (" + date + ") " + transit);
            courseTitle.setTextSize(20);
            courseTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_text));
            courseTitle.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.bmeuljirottf));
            courseCard.addView(courseTitle);

            Map<String, List<AIRecommendResponse.detailPlace>> dayGrouped = new LinkedHashMap<>();
            for (AIRecommendResponse.detailPlace place : group.places) {
                dayGrouped.computeIfAbsent(place.day, k -> new ArrayList<>()).add(place);
            }
            for (Map.Entry<String, List<AIRecommendResponse.detailPlace>> entry : dayGrouped.entrySet()) {
                String day = entry.getKey();
                List<AIRecommendResponse.detailPlace> places = entry.getValue();

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

                for (AIRecommendResponse.detailPlace place : places) {
                    View placeCard = inflater.inflate(R.layout.fragment_ai_recommend_result_list, courseCard, false);
                    TextView placeText = placeCard.findViewById(R.id.ai_result_place);
                    placeText.setText(place.placeName);
                    courseCard.addView(placeCard);
                    tripType.add(place.placeType);
                }
            }
            aiList.addView(courseCard);
        }

        aiSelectBtn.setOnClickListener(v -> {
            boolean hasSelection = false;
            int selectedCourseId = -1;

            for (int i = 0; i < aiList.getChildCount(); i++) {
                View child = aiList.getChildAt(i);
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
            AIRecommendResponse.CourseDetailGroup foundData = null;
            for (AIRecommendResponse.CourseDetailGroup d : data.courseDetails) {
                if (d.courseIdx == selectedCourseId) {
                    foundData = d;
                    break;
                }
            }

            if (foundData != null) {
                AIRecommendResponse.CourseDetailGroup selectedData = foundData;
                List<String> tripTypeList = new ArrayList<>(tripType);
                Log.d("tripType",tripTypeList.toString());
                AISaveRequest aiSaveRequest = new AISaveRequest(
                        data.modelName,
                        data.modelType,
                        selectedData.area,
                        transit,
                        (String) titleView.getText(),
                        tripTypeList,
                        new ArrayList<AIRecommendResponse.CourseDetailGroup>(){{
                            add(selectedData);
                        }}
                );

                Call<AISaveResponse> call = apiService.aiSave(aiSaveRequest);
                call.enqueue(new Callback<AISaveResponse>() {
                    @Override
                    public void onResponse(Call<AISaveResponse> call, Response<AISaveResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            AISaveResponse res = response.body();
                            Log.d("aiSave", res.resultMessage);
                            if (res.resultCode == 200) {
                                Log.d("aiSave", "성공");
                                Bundle result = new Bundle();
                                result.putBoolean("refresh_need", true);
                                getParentFragmentManager().setFragmentResult("refresh_course", result);

                                requireActivity().getSupportFragmentManager()
                                        .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                        } else {
                            Log.d("aiSave", "실패");
                            Log.d("aiSave", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<AISaveResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

        return view;
    }
}
