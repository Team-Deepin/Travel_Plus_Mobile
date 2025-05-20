package com.example.travelplus.course;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseHistoryFragment extends Fragment {
    ScrollView coursePastScrollView;
    LinearLayout coursePastListLayout;
    ConstraintLayout noCoursePastLayout;
    ApiService apiService;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past_course, container, false);
        coursePastListLayout = view.findViewById(R.id.course_past_list);
        coursePastScrollView = view.findViewById(R.id.course_past_scroll);
        noCoursePastLayout = view.findViewById(R.id.course_past_no_list);
        TextView xBtn = view.findViewById(R.id.x_btn);
        apiService = RetrofitClient.getApiInstance(requireContext()).create(ApiService.class);
        courseList(inflater);
        xBtn.setOnClickListener(view1 -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
    private void courseList(LayoutInflater inflater){
        Call<CourseHistoryResponse> call = apiService.courseHistory();
        call.enqueue(new Callback<CourseHistoryResponse>() {
            @Override
            public void onResponse(Call<CourseHistoryResponse> call, Response<CourseHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CourseHistoryResponse res = response.body();
                    Log.d("courseHistory",res.resultMessage);
                    if(res.resultCode == 200 && res.data != null && !res.data.isEmpty()){
                        Log.d("course","성공");
                        coursePastScrollView.setVisibility(VISIBLE);
                        noCoursePastLayout.setVisibility(GONE);
                        for (CourseHistoryResponse.Course course : res.data) {
                            View card = inflater.inflate(R.layout.fragment_course_list, coursePastListLayout, false);

                            TextView title = card.findViewById(R.id.course_title);
                            TextView duration = card.findViewById(R.id.course_duration);
                            TextView meansTP = card.findViewById(R.id.course_meansTP);
                            String durationText = calculateDuration(course.startDate, course.endDate);

                            title.setText(course.title);
                            duration.setText(durationText);
                            meansTP.setText(course.meansTp);

                            coursePastListLayout.addView(card);
                            card.setOnClickListener(view1 -> {
                                // 상세 코스 리스트 UI 띄우기
                                Bundle bundle = new Bundle();
                                bundle.putInt("courseId",course.courseId);
                                bundle.putString("title", course.title);
                                bundle.putString("location", course.area);
                                bundle.putString("duration", durationText);
                                bundle.putString("meansTP", course.meansTp);
                                bundle.putString("type", course.tripType);

                                ConstraintLayout coursePastLayout = requireView().findViewById(R.id.course_past_layout);
                                coursePastLayout.setVisibility(GONE);

                                if (course.meansTp.equals("자가용")){
                                    CourseDetailCarFragment detailCarFragment = new CourseDetailCarFragment();
                                    detailCarFragment.setArguments(bundle);
                                    requireActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.course_fragment_container, detailCarFragment)
                                            .addToBackStack(null)
                                            .commit();
                                } else if (course.meansTp.equals("대중교통")) {
                                    CourseDetailTransitFragment detailTransitFragment = new CourseDetailTransitFragment();
                                    detailTransitFragment.setArguments(bundle);
                                    requireActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.course_fragment_container, detailTransitFragment)
                                            .addToBackStack(null)
                                            .commit();
                                }
                            });
                        }
                    }else{
                        coursePastScrollView.setVisibility(GONE);
                        noCoursePastLayout.setVisibility(VISIBLE);
                        Log.d("courseHistory", "코스 데이터 없음 또는 실패");
                    }
                }else{
                    coursePastScrollView.setVisibility(GONE);
                    noCoursePastLayout.setVisibility(VISIBLE);
                    Log.d("courseHistory","연결 실패");
                }
            }

            @Override
            public void onFailure(Call<CourseHistoryResponse> call, Throwable t) {
                coursePastScrollView.setVisibility(GONE);
                noCoursePastLayout.setVisibility(VISIBLE);
                Log.d("courseHistory","연결 실패");
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
}
