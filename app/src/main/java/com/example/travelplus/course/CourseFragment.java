package com.example.travelplus.course;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;
import com.example.travelplus.recommend.AIRecommendFragment;
import com.example.travelplus.survey.SurveyFragment;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseFragment extends Fragment {
    CardView tripRecommend;
    MaterialCardView aiRecommend;
    ScrollView courseScrollView;
    LinearLayout courseListLayout;
    ConstraintLayout noCourseListLayout;
    ApiService apiService;
    ImageView pastCourseBtn;
    ShimmerFrameLayout skeletonUI;

    @Override
    public void onResume() {
        super.onResume();
        if (apiService == null) {
            apiService = RetrofitClient.getApiInstance(requireContext()).create(ApiService.class);
        }
        courseList(LayoutInflater.from(requireContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        courseListLayout = view.findViewById(R.id.course_list);
        courseScrollView = view.findViewById(R.id.course_scroll);
        noCourseListLayout = view.findViewById(R.id.course_no_list);
        aiRecommend = view.findViewById(R.id.course_AI);
        tripRecommend = view.findViewById(R.id.course_trip);
        pastCourseBtn = view.findViewById(R.id.past_course_btn);
        apiService = RetrofitClient.getApiInstance(requireContext()).create(ApiService.class);
        skeletonUI = view.findViewById(R.id.course_skeleton);

        aiRecommend.setOnClickListener(view1 -> {
            ai_recommend_click();
        });
        tripRecommend.setOnClickListener(view1 -> {
            survey_click();
        });
        pastCourseBtn.setOnClickListener(view1 -> {
            showPast();
        });
        requireActivity().getSupportFragmentManager()
                .addOnBackStackChangedListener(()->{
                    if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() == 0){
                        ConstraintLayout courseLayout = view.findViewById(R.id.course_layout);
                        FloatingActionButton plusFab = requireActivity().findViewById(R.id.detail_plus_fab);
                        FloatingActionButton cancelFab = requireActivity().findViewById(R.id.detail_cancel_fab);
                        FloatingActionButton deleteFab = requireActivity().findViewById(R.id.detail_delete_fab);
                        FloatingActionButton rateFab = requireActivity().findViewById(R.id.detail_rate_fab);
                        TextView deleteText = requireActivity().findViewById(R.id.detail_delete_text);
                        TextView rateText = requireActivity().findViewById(R.id.detail_rate_text);
                        View detailBackground = requireActivity().findViewById(R.id.detail_background);
                        courseLayout.setVisibility(VISIBLE);
                        plusFab.setVisibility(GONE);
                        detailBackground.setVisibility(GONE);
                        cancelFab.setVisibility(GONE);
                        deleteFab.setVisibility(GONE);
                        rateFab.setVisibility(GONE);
                        deleteText.setVisibility(GONE);
                        rateText.setVisibility(GONE);
                    }
                });

        getParentFragmentManager().setFragmentResultListener("refresh_course", this, (requestKey, bundle) -> {
            boolean refresh = bundle.getBoolean("refresh_need", false);
            if (refresh) {
                courseList(LayoutInflater.from(requireContext()));
            }
        });

        return view;
    }
    private void courseList(LayoutInflater inflater){
        skeletonUI.setVisibility(View.VISIBLE);
        skeletonUI.startShimmer();
        courseScrollView.setVisibility(View.GONE);
        noCourseListLayout.setVisibility(View.GONE);
        Call<CourseResponse> call = apiService.course();
        call.enqueue(new Callback<CourseResponse>() {
            @Override
            public void onResponse(Call<CourseResponse> call, Response<CourseResponse> response) {
                skeletonUI.stopShimmer();
                skeletonUI.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    CourseResponse res = response.body();
                    Log.d("course",res.resultMessage);
                    if(res.resultCode == 200 && res.data != null && !res.data.isEmpty()){
                        Log.d("course","성공");
                        courseListLayout.removeAllViews();
                        courseScrollView.setVisibility(VISIBLE);
                        noCourseListLayout.setVisibility(GONE);
                        for (CourseResponse.Course course : res.data) {
                            View card = inflater.inflate(R.layout.fragment_course_list, courseListLayout, false);

                            TextView title = card.findViewById(R.id.course_title);
                            TextView duration = card.findViewById(R.id.course_duration);
                            TextView meansTp = card.findViewById(R.id.course_meansTP);
                            String durationText = calculateDuration(course.startDate, course.endDate);

                            title.setText(course.title);
                            duration.setText(durationText);
                            meansTp.setText(course.meansTp);

                            courseListLayout.addView(card);
                            card.setOnClickListener(view1 -> {
                                // 상세 코스 리스트 UI 띄우기
                                Bundle bundle = new Bundle();
                                bundle.putInt("courseId",course.courseId);
                                bundle.putString("title", course.title);
                                bundle.putString("location", course.area);
                                bundle.putString("duration", durationText);
                                bundle.putString("meansTp", course.meansTp);
                                bundle.putString("type", course.tripType);

                                ConstraintLayout courseLayout = requireView().findViewById(R.id.course_layout);
                                courseLayout.setVisibility(GONE);

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
                skeletonUI.stopShimmer();
                skeletonUI.setVisibility(View.GONE);
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

            if (diff == 0) {
                return "당일치기";
            } else {
                return diff + "박 " + (diff + 1) + "일";
            }
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
    private void showPast(){
        ConstraintLayout courseLayout = requireView().findViewById(R.id.course_layout);
        courseLayout.setVisibility(GONE);
        CourseHistoryFragment coursePastFragment = new CourseHistoryFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.course_fragment_container, coursePastFragment)
                .addToBackStack(null)
                .commit();
    }
}
