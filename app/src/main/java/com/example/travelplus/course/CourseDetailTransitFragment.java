package com.example.travelplus.course;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;

import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CourseDetailTransitFragment extends Fragment {
    String title, duration, location;
    int courseId;
    FloatingActionButton plusFab, cancelFab, deleteFab, rateFab;
    TextView deleteText, rateText, titleView, locationView, durationView, vehicleView;
    View detailBackground;
    LinearLayout detailListLayout;
    ApiService apiService;
    ShimmerFrameLayout detailSkeleton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getInt("courseId");
            title = getArguments().getString("title");
            duration = getArguments().getString("duration");
            location = getArguments().getString("location");
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_detail, container, false);
        titleView = view.findViewById(R.id.detail_title);
        locationView = view.findViewById(R.id.detail_location);
        durationView = view.findViewById(R.id.detail_duration);
        vehicleView = view.findViewById(R.id.detail_vehicle);
        plusFab = requireActivity().findViewById(R.id.detail_plus_fab);
        cancelFab = requireActivity().findViewById(R.id.detail_cancel_fab);
        deleteFab = requireActivity().findViewById(R.id.detail_delete_fab);
        rateFab = requireActivity().findViewById(R.id.detail_rate_fab);
        deleteText = requireActivity().findViewById(R.id.detail_delete_text);
        rateText = requireActivity().findViewById(R.id.detail_rate_text);
        detailBackground = requireActivity().findViewById(R.id.detail_background);
        detailListLayout = view.findViewById(R.id.detail_list);
        plusFab.setVisibility(VISIBLE);
        ImageView mapView = view.findViewById(R.id.detail_map);
        detailSkeleton = view.findViewById(R.id.detail_skeleton);
        apiService = RetrofitClient.getApiInstance(requireContext()).create(ApiService.class);
        showDetails(inflater);

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
        rateFab.setOnClickListener(view1 -> {
            showRatingPopup();
        });
        mapView.setOnClickListener(view1 -> {
            showMapDialog();
        });


        return view;
    }
    private void showMapDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.pop_up_map);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        TextView quit = dialog.findViewById(R.id.quit);
        quit.setOnClickListener(view -> {
            dialog.dismiss();
        });

        WebView webView = dialog.findViewById(R.id.pop_up_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/map.html");

        dialog.show();
    }
    private void showDetails(LayoutInflater inflater){
        detailSkeleton.setVisibility(View.VISIBLE);
        detailSkeleton.startShimmer();
        detailListLayout.removeAllViews();
        Log.d("showDetailsTransit", "apiService 호출 시작");
        Call<CourseDetailTransitResponse> call = apiService.detailTransit(courseId);
        call.enqueue(new Callback<CourseDetailTransitResponse>() {
            @Override
            public void onResponse(Call<CourseDetailTransitResponse> call, Response<CourseDetailTransitResponse> response) {
                detailSkeleton.stopShimmer();
                detailSkeleton.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    CourseDetailTransitResponse res = response.body();
                    Log.d("courseDetailTransit",res.resultMessage);
                    if(res.resultCode == 200 && res.data != null && !res.data.isEmpty()) {
                        titleView.setText(title);
                        locationView.setText(location);
                        vehicleView.setText(res.data.get(0).meansTp);
                        durationView.setText(duration+", ");
                        LinearLayout detailCard = new LinearLayout(requireContext());

                        detailCard.setTag(courseId);
                        detailCard.setOrientation(LinearLayout.VERTICAL);
                        detailCard.setBackgroundResource(R.drawable.ai_background);
                        LinearLayout.LayoutParams courseParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        courseParams.setMargins(0, 0, 0, 40);
                        detailCard.setLayoutParams(courseParams);

                        for (CourseDetailTransitResponse.transitData transitData : res.data){
                            TextView dayText = new TextView(requireContext());
                            LinearLayout.LayoutParams dayParams = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            dayParams.setMargins(30, 30, 0, 20);
                            dayText.setLayoutParams(dayParams);
                            dayText.setText("📅 " + transitData.day);
                            dayText.setTextSize(23);
                            dayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_text));
                            dayText.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.bmeuljirottf));
                            detailCard.addView(dayText);
                            for (CourseDetailTransitResponse.transitDetail transitDetail : transitData.transitDetails) {
                                View fromPlaceCard = inflater.inflate(R.layout.fragment_course_detail_transit_place_list, detailCard, false);
                                TextView placeText = fromPlaceCard.findViewById(R.id.detail_transit_place_name);
                                placeText.setText(transitDetail.from);
                                detailCard.addView(fromPlaceCard);
                                for (CourseDetailTransitResponse.path path : transitDetail.paths){
                                    View pathCard = inflater.inflate(R.layout.fragment_course_detail_transit_path_list, detailCard, false);
                                    TextView pathText = pathCard.findViewById(R.id.detail_transit_path);
                                    String mode = path.mode.equals("WALK") ? "도보" : path.mode.equals("BUS") ? "버스" : path.mode.equals("SUBWAY") ? "지하철" : "기타";
                                    String route = "";
                                    if (path.route != null){
                                        route = path.route;
                                    }
                                    int time = path.sectionTime;
                                    int hourTime = (time / 60 >= 60) ? (time / 60) / 60 : 0;
                                    int minTime = (time / 60 >= 60) ? (time % 60) : (time / 60);
                                    String timeText = hourTime !=0 ? "약 "+hourTime+"시간 "+minTime+"분 소요" : "약 "+minTime+"분 소요";
                                    pathText.setLayoutParams(new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    ));

                                    if (route == ""){
                                        pathText.setText(path.start+" -> "+path.end+" "+mode+" "+timeText);
                                    }else {
                                        pathText.setText(path.start+" -> "+path.end+" ("+mode+") "+route+" "+timeText);
                                    }

                                    detailCard.addView(pathCard);
                                }
                            }
                            if (transitData.transitDetails != null && !transitData.transitDetails.isEmpty()) {
                                CourseDetailTransitResponse.transitDetail lastDetail = transitData.transitDetails.get(transitData.transitDetails.size() - 1);
                                View toPlaceCard = inflater.inflate(R.layout.fragment_course_detail_transit_place_list, detailCard, false);
                                TextView placeText = toPlaceCard.findViewById(R.id.detail_transit_place_name);
                                placeText.setText(lastDetail.to);
                                detailCard.addView(toPlaceCard);
                            }
                        }
                        detailListLayout.addView(detailCard);
                    }else {
                        Log.d("courseDetailTransit", "데이터 없음");
                    }
                }
            }

            @Override
            public void onFailure(Call<CourseDetailTransitResponse> call, Throwable t) {
                detailSkeleton.stopShimmer();
                detailSkeleton.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
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
        deleteCancelBtn.setOnClickListener(v -> {
            plusFab.setVisibility(VISIBLE);
            detailBackground.setVisibility(GONE);
            cancelFab.setVisibility(GONE);
            deleteFab.setVisibility(GONE);
            rateFab.setVisibility(GONE);
            deleteText.setVisibility(GONE);
            rateText.setVisibility(GONE);
            dialog.dismiss();
            dialog.dismiss();
        });
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
                            Log.d("Delete Course", "코스 삭제 완료");
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
    private void showRatingPopup(){
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.pop_up_course_rate);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_input, null));
        }
        ImageView rateNextBtn = dialog.findViewById(R.id.course_rate_next_button);
        ImageView rateApplyBtn = dialog.findViewById(R.id.course_rate_apply_button);
        RatingBar ratingBar = dialog.findViewById(R.id.course_rate_star);
        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if (rating < 0.5f) {
                bar.setRating(0.5f);
            }
        });
        rateNextBtn.setOnClickListener(v ->{
            plusFab.setVisibility(VISIBLE);
            detailBackground.setVisibility(GONE);
            cancelFab.setVisibility(GONE);
            deleteFab.setVisibility(GONE);
            rateFab.setVisibility(GONE);
            deleteText.setVisibility(GONE);
            rateText.setVisibility(GONE);
            dialog.dismiss();
        });
        rateApplyBtn.setOnClickListener(v -> {
            // 코스 평가 API
            double score = ratingBar.getRating();
            CourseRatingRequest courseRatingRequest = new CourseRatingRequest(courseId, score);
            Call<CourseRatingResponse> call = apiService.rate(courseRatingRequest);
            call.enqueue(new Callback<CourseRatingResponse>() {
                @Override
                public void onResponse(Call<CourseRatingResponse> call, Response<CourseRatingResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CourseRatingResponse res = response.body();
                        Log.d("Rate Course",res.resultMessage);
                        if (res.resultCode == 200) {
                            Log.d("Rate Course", "평가 점수 : "+score);
                            Toast.makeText(getActivity(), "코스가 평가되었습니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }else{
                            Toast.makeText(getActivity(), "코스 평가 실패", Toast.LENGTH_SHORT).show();
                            Log.d("Rate Course",String.valueOf(res.resultCode));
                            dialog.dismiss();
                        }
                    }else {
                        Toast.makeText(getActivity(), "코스 평가 실패", Toast.LENGTH_SHORT).show();
                        Log.d("Rate Course","코스 평가 실패");
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<CourseRatingResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), "코스 평가 실패", Toast.LENGTH_SHORT).show();
                    Log.d("Rate Course","서버 연결 실패");
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }
}
