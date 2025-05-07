package com.example.travelplus.course;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.travelplus.R;
import com.example.travelplus.login.LoginActivity;
import com.example.travelplus.login.LogoutResponse;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.recommend.AIRecommendFragment;
import com.example.travelplus.survey.SurveyFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CourseFragment extends Fragment {
    ImageView aiRecommend, tripRecommend;
    ScrollView courseScrollView;
    LinearLayout courseListLayout;
    ConstraintLayout noCourseListLayout;
    ApiService apiService;
    private MockWebServer mockServer;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        setupMockServer(inflater);
        courseListLayout = view.findViewById(R.id.course_list);
        courseScrollView = view.findViewById(R.id.course_scroll);
        noCourseListLayout = view.findViewById(R.id.course_no_list);
        aiRecommend = view.findViewById(R.id.course_AI);
        tripRecommend = view.findViewById(R.id.course_trip);

        aiRecommend.setOnClickListener(view1 -> {
            ai_recommend_click();
        });
        tripRecommend.setOnClickListener(view1 -> {
            survey_click();
        });

        requireActivity().getSupportFragmentManager()
                .addOnBackStackChangedListener(()->{
                    if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() == 0){
                        ConstraintLayout courseLayout = view.findViewById(R.id.course_layout);
                        FloatingActionButton cancelFab = requireActivity().findViewById(R.id.detail_cancel_fab);
                        FloatingActionButton deleteFab = requireActivity().findViewById(R.id.detail_delete_fab);
                        FloatingActionButton rateFab = requireActivity().findViewById(R.id.detail_rate_fab);
                        TextView deleteText = requireActivity().findViewById(R.id.detail_delete_text);
                        TextView rateText = requireActivity().findViewById(R.id.detail_rate_text);
                        View detailBackground = requireActivity().findViewById(R.id.detail_background);
                        courseLayout.setVisibility(VISIBLE);
                        detailBackground.setVisibility(GONE);
                        cancelFab.setVisibility(GONE);
                        deleteFab.setVisibility(GONE);
                        rateFab.setVisibility(GONE);
                        deleteText.setVisibility(GONE);
                        rateText.setVisibility(GONE);
                    }
                });
        return view;
    }
    private void courseList(LayoutInflater inflater){
        Call<CourseResponse> call = apiService.course();
        call.enqueue(new Callback<CourseResponse>() {
            @Override
            public void onResponse(Call<CourseResponse> call, Response<CourseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CourseResponse res = response.body();
                    Log.d("course",res.resultMessage);
                    if(res.resultCode == 200 && res.data != null && !res.data.isEmpty()){
                        Log.d("course","성공");
                        courseScrollView.setVisibility(VISIBLE);
                        noCourseListLayout.setVisibility(GONE);
                        for (Course course : res.data) {
                            View card = inflater.inflate(R.layout.fragment_course_list, courseListLayout, false);

                            TextView title = card.findViewById(R.id.course_title);
                            TextView duration = card.findViewById(R.id.course_duration);
                            TextView meansTP = card.findViewById(R.id.course_meansTP);
                            String durationText = calculateDuration(course.startDate, course.endDate);

                            title.setText(course.title);
                            duration.setText(durationText);
                            meansTP.setText(course.meansTP);

                            courseListLayout.addView(card);
                            card.setOnClickListener(view1 -> {
                                // 상세 코스 리스트 UI 띄우기
                                Bundle bundle = new Bundle();
                                bundle.putInt("courseId",course.courseId);
                                bundle.putString("title", course.title);
                                bundle.putString("location", course.area);
                                bundle.putString("duration", durationText);
                                bundle.putString("meansTP", course.meansTP);
                                bundle.putStringArrayList("type", new ArrayList<>(course.courseType));

                                CourseDetailFragment detailFragment = new CourseDetailFragment();
                                detailFragment.setArguments(bundle);
                                ConstraintLayout courseLayout = requireView().findViewById(R.id.course_layout);
                                courseLayout.setVisibility(GONE);
                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.course_fragment_container, detailFragment)
                                        .addToBackStack(null)
                                        .commit();
                            });
                        }
                    }else{
                        courseScrollView.setVisibility(GONE);
                        noCourseListLayout.setVisibility(VISIBLE);
                        Log.d("course", "코스 데이터 없음 또는 실패");
                    }
                }else{
                    courseScrollView.setVisibility(GONE);
                    noCourseListLayout.setVisibility(VISIBLE);
                    Log.d("course","연결 실패");
                }
            }

            @Override
            public void onFailure(Call<CourseResponse> call, Throwable t) {
                courseScrollView.setVisibility(GONE);
                noCourseListLayout.setVisibility(VISIBLE);
                Log.d("course","연결 실패");
            }
        });
    }
    private String calculateDuration(String start, String end) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);
            long diff = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
            return diff + "박 " + (diff + 1) + "일";
        } catch (Exception e) {
            e.printStackTrace();
            return "기간 불명";
        }
    }
    private void ai_recommend_click(){
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.pop_up_course_title);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_input, null));
        }
        EditText courseTitle = dialog.findViewById(R.id.course_create_title);
        ImageView inputBtn = dialog.findViewById(R.id.course_jnput_button);
        ImageView nextBtn = dialog.findViewById(R.id.course_next_button);
        nextBtn.setOnClickListener(v -> dialog.dismiss());
        inputBtn.setOnClickListener(v -> {
            String title = courseTitle.getText().toString().trim();
            Bundle bundle = new Bundle();
            bundle.putString("title",title);
            AIRecommendFragment aiRecommendFragment = new AIRecommendFragment();
            aiRecommendFragment.setArguments(bundle);
            ConstraintLayout courseLayout = requireView().findViewById(R.id.course_layout);
            courseLayout.setVisibility(GONE);
            dialog.dismiss();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.course_fragment_container, aiRecommendFragment)
                    .addToBackStack(null)
                    .commit();
        });
        dialog.show();
    }
    private void survey_click(){
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.pop_up_course_title);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_input, null));
        }
        EditText courseTitle = dialog.findViewById(R.id.course_create_title);
        ImageView inputBtn = dialog.findViewById(R.id.course_jnput_button);
        ImageView nextBtn = dialog.findViewById(R.id.course_next_button);
        nextBtn.setOnClickListener(v -> dialog.dismiss());
        inputBtn.setOnClickListener(v -> {
            String title = courseTitle.getText().toString().trim();
            Bundle bundle = new Bundle();
            bundle.putString("title",title);
            SurveyFragment surveyFragment = new SurveyFragment();
            surveyFragment.setArguments(bundle);
            ConstraintLayout courseLayout = requireView().findViewById(R.id.course_layout);
            courseLayout.setVisibility(GONE);
            dialog.dismiss();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.course_fragment_container, surveyFragment)
                    .addToBackStack(null)
                    .commit();
        });
        dialog.show();
    }
    private void setupMockServer(LayoutInflater inflater) {
        new Thread(() -> {
            try {
                mockServer = new MockWebServer();
                mockServer.enqueue(new MockResponse()
                        .setResponseCode(200)
                        .setBody("{\n" +
                                "  \"resultCode\": 200,\n" +
                                "  \"resultMessage\": \"Success\",\n" +
                                "  \"data\": [\n" +
                                "    {\n" +
                                "      \"courseId\": 1,\n" +
                                "      \"title\": \"trip_123\",\n" +
                                "      \"area\": \"Busan\",\n" +
                                "      \"courseType\": [\"힐링\", \"쇼핑\"],\n" +
                                "      \"startDate\": \"2025-03-01\",\n" +
                                "      \"endDate\": \"2025-03-05\",\n" +
                                "      \"meansTP\": \"자가용\"\n" +
                                "    },\n" +
                                "    {\n" +
                                "      \"courseId\": 2,\n" +
                                "      \"title\": \"trip_124\",\n" +
                                "      \"area\": \"Seoul\",\n" +
                                "      \"courseType\": [\"힐링\", \"쇼핑\"],\n" +
                                "      \"startDate\": \"2025-06-15\",\n" +
                                "      \"endDate\": \"2025-06-20\",\n" +
                                "      \"meansTP\": \"자가용\"\n" +
                                "    }\n" +
                                "  ]\n" +
                                "}"));

                mockServer.start();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(mockServer.url("/"))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                apiService = retrofit.create(ApiService.class);

                getActivity().runOnUiThread(() -> courseList(inflater));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
