package com.example.travelplus.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.travelplus.CourseList;
import com.example.travelplus.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;

public class CourseFragment extends Fragment {
    List<CourseList> courseListFromDB = Arrays.asList(
            new CourseList("제주도 2박 3일", "제주도","2박 3일,", "자가용"),
            new CourseList("부산 1박 2일", "부산","1박 2일,", "기차"),
            new CourseList("서울 당일치기", "서울","당일치기,", "지하철"),
            new CourseList("test", "test","test", "test"),
            new CourseList("test", "test","test", "test"),
            new CourseList("test", "test","test", "test")
    );
    ImageView aiRecommend, tripRecomment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        LinearLayout courseListLayout = view.findViewById(R.id.course_list);
        ScrollView courseScrollView = view.findViewById(R.id.course_scroll);
        ConstraintLayout noCourseListLayout = view.findViewById(R.id.course_no_list);
        aiRecommend = view.findViewById(R.id.course_AI);
        tripRecomment = view.findViewById(R.id.course_trip);

        if(courseListFromDB.isEmpty()){
            courseScrollView.setVisibility(GONE);
            noCourseListLayout.setVisibility(VISIBLE);
        }else{
            courseScrollView.setVisibility(VISIBLE);
            noCourseListLayout.setVisibility(GONE);
        }

        for (CourseList course : courseListFromDB) {
            View card = inflater.inflate(R.layout.fragment_course_list, courseListLayout, false);

            CardView cardList = card.findViewById(R.id.card_list);
            TextView title = card.findViewById(R.id.course_title);
            TextView duration = card.findViewById(R.id.course_duration);
            TextView meansTP = card.findViewById(R.id.course_meansTP);

            title.setText(course.title);
            duration.setText(course.duration);
            meansTP.setText(course.meansTP);

            courseListLayout.addView(card);
            card.setOnClickListener(view1 -> {
                // 상세 코스 리스트 UI 띄우기
                Bundle bundle = new Bundle();
                bundle.putString("title", course.title);
                bundle.putString("location", course.location);
                bundle.putString("duration", course.duration);
                bundle.putString("meansTP", course.meansTP);

                CourseDetailFragment detailFragment = new CourseDetailFragment();
                detailFragment.setArguments(bundle);
                ConstraintLayout courseLayout = view.findViewById(R.id.course_layout);
                courseLayout.setVisibility(GONE);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.course_fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }

        aiRecommend.setOnClickListener(view1 -> {
            // AI 추천 UI 띄우기
        });
        tripRecomment.setOnClickListener(view1 -> {
            // 여행지 추천 UI 띄우기
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
}
