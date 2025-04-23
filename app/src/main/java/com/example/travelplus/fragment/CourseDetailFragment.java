package com.example.travelplus.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travelplus.CourseDetailList;
import com.example.travelplus.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;

public class CourseDetailFragment extends Fragment {
    private String title, duration, vehicle, location;
    int cnt=0;
    List<CourseDetailList> courseDetailListFromDB = Arrays.asList(
            new CourseDetailList(200, "성공","자가용","이동지1","이동지2",30,"없음",30.5,"이동수단"),
            new CourseDetailList(200, "성공","자가용","이동지2","이동지3",30,"없음",30.5,"이동수단"),
            new CourseDetailList(200, "성공","자가용","이동지3","이동지4",30,"없음",30.5,"이동수단")
    );
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            location = getArguments().getString("location");
            duration = getArguments().getString("duration");
            vehicle = getArguments().getString("vehicle");
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_detail, container, false);

        TextView titleView = view.findViewById(R.id.detail_title);
        TextView locationView = view.findViewById(R.id.detail_location);
        TextView durationView = view.findViewById(R.id.detail_duration);
        TextView vehicleView = view.findViewById(R.id.detail_vehicle);
        FloatingActionButton plusFab = view.findViewById(R.id.detail_plus_fab);
        FloatingActionButton cancelFab = requireActivity().findViewById(R.id.detail_cancel_fab);
        FloatingActionButton deleteFab = requireActivity().findViewById(R.id.detail_delete_fab);
        FloatingActionButton rateFab = requireActivity().findViewById(R.id.detail_rate_fab);
        TextView deleteText = requireActivity().findViewById(R.id.detail_delete_text);
        TextView rateText = requireActivity().findViewById(R.id.detail_rate_text);
        View detailBackground = requireActivity().findViewById(R.id.detail_background);
        LinearLayout detailListLayout = view.findViewById(R.id.detail_list);

        titleView.setText(title);
        locationView.setText(location);
        durationView.setText(duration);
        vehicleView.setText(vehicle);
        plusFab.setOnClickListener(view1 -> {
            plusFab.setVisibility(GONE);
            detailBackground.setVisibility(VISIBLE);
            cancelFab.setVisibility(VISIBLE);
            deleteFab.setVisibility(VISIBLE);
            rateFab.setVisibility(VISIBLE);
            deleteText.setVisibility(VISIBLE);
            rateText.setVisibility(VISIBLE);
        });
        cancelFab.setOnClickListener(view1 -> {
            plusFab.setVisibility(VISIBLE);
            detailBackground.setVisibility(GONE);
            cancelFab.setVisibility(GONE);
            deleteFab.setVisibility(GONE);
            rateFab.setVisibility(GONE);
            deleteText.setVisibility(GONE);
            rateText.setVisibility(GONE);
        });

        for (CourseDetailList course : courseDetailListFromDB){
            View detail = inflater.inflate(R.layout.fragment_course_detail_list, detailListLayout, false);

            TextView place = detail.findViewById(R.id.place_name);
            TextView vehicle = detail.findViewById(R.id.vehicle);
            TextView time = detail.findViewById(R.id.time);

            place.setText(course.from);
            if(cnt == courseDetailListFromDB.size()) break;
            vehicle.setText(course.meansTp);
            String takenTime = String.valueOf(course.sectionTime);
            time.setText(takenTime+"분 소요");
            cnt++;
            detailListLayout.addView(detail);
        }

        CourseDetailList last = courseDetailListFromDB.get(courseDetailListFromDB.size() - 1);
        View lastPlaceView = inflater.inflate(R.layout.fragment_course_detail_list_last, detailListLayout, false);

        TextView lastPlace = lastPlaceView.findViewById(R.id.place_name);
        lastPlace.setText(last.to);
        detailListLayout.addView(lastPlaceView);

        return view;
    }
}
