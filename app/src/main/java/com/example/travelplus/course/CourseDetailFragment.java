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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travelplus.CourseDetailList;
import com.example.travelplus.R;
import com.example.travelplus.login.LoginActivity;
import com.example.travelplus.login.LogoutResponse;
import com.example.travelplus.network.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CourseDetailFragment extends Fragment {
    private String title, duration, meansTP, location;
    int cnt=0;
    int courseId;
    ApiService apiService;
    private MockWebServer mockServer;
    List<CourseDetailList> courseDetailListFromDB = Arrays.asList(
            new CourseDetailList(200, "성공","자가용","이동지1","이동지2",30,"없음",30.5,"이동수단"),
            new CourseDetailList(200, "성공","자가용","이동지2","이동지3",30,"없음",30.5,"이동수단"),
            new CourseDetailList(200, "성공","자가용","이동지3","이동지4",30,"없음",30.5,"이동수단")
    );
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getInt("courseId");
            title = getArguments().getString("title");
            location = getArguments().getString("location");
            duration = getArguments().getString("duration");
            meansTP = getArguments().getString("meansTP");
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_detail, container, false);
        setupMockServer();
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
        vehicleView.setText(meansTP);
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
        detailBackground.setOnClickListener(view1 -> {
            plusFab.setVisibility(VISIBLE);
            detailBackground.setVisibility(GONE);
            cancelFab.setVisibility(GONE);
            deleteFab.setVisibility(GONE);
            rateFab.setVisibility(GONE);
            deleteText.setVisibility(GONE);
            rateText.setVisibility(GONE);
        });
        deleteFab.setOnClickListener(view1 -> {
            showDeletePopup();
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
    private void showDeletePopup(){
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.pop_up_course_delete);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_input, null));
        }
        ImageView deleteCancelBtn = dialog.findViewById(R.id.course_delete_cancel_button);
        ImageView deleteBtn = dialog.findViewById(R.id.course_delete_button);
        deleteCancelBtn.setOnClickListener(v -> dialog.dismiss());
        deleteBtn.setOnClickListener(v -> {
            // 코스 삭제 API
            Call<CourseDeleteResponse> call = apiService.deleteCourse(courseId);
            call.enqueue(new Callback<CourseDeleteResponse>() {
                @Override
                public void onResponse(Call<CourseDeleteResponse> call, Response<CourseDeleteResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CourseDeleteResponse res = response.body();
                        Log.d("Delete Course",res.resultMessage);
                        if (res.resultCode == 200) {
                            Toast.makeText(getActivity(), "코스가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }else{
                            Toast.makeText(getActivity(), "코스 삭제 실패", Toast.LENGTH_SHORT).show();
                            Log.d("Delete Course",String.valueOf(res.resultCode));
                            dialog.dismiss();
                        }
                    }else {
                        Toast.makeText(getActivity(), "코스 삭제 실패", Toast.LENGTH_SHORT).show();
                        Log.d("Delete Course","코스 삭제 실패");
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<CourseDeleteResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), "코스 삭제 실패", Toast.LENGTH_SHORT).show();
                    Log.d("Delete Course","서버 연결 실패");
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
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
        }).start();
    }
}
