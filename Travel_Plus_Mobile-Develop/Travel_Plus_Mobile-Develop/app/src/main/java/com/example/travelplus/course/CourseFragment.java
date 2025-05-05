package com.example.travelplus.course;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseFragment extends Fragment {
    ImageView aiRecommend, tripRecommend;
    ScrollView courseScrollView;
    LinearLayout courseListLayout;
    ConstraintLayout noCourseListLayout;
    ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        apiService = RetrofitClient.getInstance().create(ApiService.class);

        courseListLayout = view.findViewById(R.id.course_list);
        courseScrollView = view.findViewById(R.id.course_scroll);
        noCourseListLayout = view.findViewById(R.id.course_no_list);
        aiRecommend = view.findViewById(R.id.course_AI);
        tripRecommend = view.findViewById(R.id.course_trip);

        courseList(inflater);

        aiRecommend.setOnClickListener(view1 -> {
            // AI 추천 UI 띄우기
        });

        tripRecommend.setOnClickListener(view1 -> {
            // 여행지 추천 UI 띄우기
        });

        requireActivity().getSupportFragmentManager()
                .addOnBackStackChangedListener(() -> {
                    if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() == 0) {
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

    private void courseList(LayoutInflater inflater) {
        Long userId = 1L; // TODO: 실제 로그인한 사용자 ID로 대체
        Call<CourseResponse> call = apiService.course(userId);
        call.enqueue(new Callback<CourseResponse>() {
            @Override
            public void onResponse(Call<CourseResponse> call, Response<CourseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CourseResponse res = response.body();
                    Log.d("course", res.resultMessage);
                    if (res.resultCode == 200 && res.data != null && !res.data.isEmpty()) {
                        Log.d("course", "성공");
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
                                Bundle bundle = new Bundle();
                                bundle.putInt("courseId", course.courseId);
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
                    } else {
                        courseScrollView.setVisibility(GONE);
                        noCourseListLayout.setVisibility(VISIBLE);
                        Log.d("course", "코스 데이터 없음 또는 실패");
                    }
                } else {
                    courseScrollView.setVisibility(GONE);
                    noCourseListLayout.setVisibility(VISIBLE);
                    Log.d("course", "연결 실패");
                }
            }

            @Override
            public void onFailure(Call<CourseResponse> call, Throwable t) {
                courseScrollView.setVisibility(GONE);
                noCourseListLayout.setVisibility(GONE);
                Log.e("course", "네트워크 오류", t);
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
