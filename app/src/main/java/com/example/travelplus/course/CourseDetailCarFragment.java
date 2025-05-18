package com.example.travelplus.course;

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
import android.webkit.WebViewClient;
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

import com.example.travelplus.CarDay;
import com.example.travelplus.CarLocation;
import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CourseDetailCarFragment extends Fragment {
    String title, duration, meansTP, location, authorization;
    int courseId;
    FloatingActionButton plusFab, cancelFab, deleteFab, rateFab;
    TextView deleteText, rateText, titleView, locationView, durationView, vehicleView;
    View detailBackground;
    LinearLayout detailListLayout;
    ApiService apiService;
    MockWebServer mockServer;
    SharedPreferences prefs;
    List<CarDay> carDays = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getInt("courseId");
            title = getArguments().getString("title");
            duration = getArguments().getString("duration");
            location = getArguments().getString("location");
        }
        prefs = requireContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        authorization = prefs.getString("authorization", null);
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
        ImageView mapView = view.findViewById(R.id.detail_map);
        plusFab.setVisibility(VISIBLE);
        setupMockServer(() -> requireActivity().runOnUiThread(() -> showDetails(inflater)));

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

        String json = new Gson().toJson(carDays);
        String safeJson = JSONObject.quote(json);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.evaluateJavascript("setRouteData(" + safeJson + ");", null);
            }
        });

        webView.loadUrl("file:///android_asset/map.html");

        dialog.show();
    }
    private void showDetails(LayoutInflater inflater){
        Log.d("showDetailsCar", "apiService 호출 시작");
        Call<CourseDetailCarResponse> call = apiService.detailCar(authorization, courseId);
        call.enqueue(new Callback<CourseDetailCarResponse>() {
            @Override
            public void onResponse(Call<CourseDetailCarResponse> call, Response<CourseDetailCarResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CourseDetailCarResponse res = response.body();
                    Log.d("courseDetailCar",res.resultMessage);
                    if(res.resultCode == 200 && res.data != null && !res.data.isEmpty()) {
                        titleView.setText(title);
                        locationView.setText(location);
                        vehicleView.setText("자가용");
                        durationView.setText(duration+ ", ");
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

                        for (CourseDetailCarResponse.carData carData : res.data){
                            List<CarLocation> carLocations = new ArrayList<>();
                            TextView dayText = new TextView(requireContext());
                            LinearLayout.LayoutParams dayParams = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            dayParams.setMargins(30, 30, 0, 20);
                            dayText.setLayoutParams(dayParams);
                            dayText.setText("📅 " + carData.day);
                            dayText.setTextSize(23);
                            dayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_text));
                            dayText.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.bmeuljirottf));
                            detailCard.addView(dayText);

                            for (CourseDetailCarResponse.route route : carData.routes) {
                                View placeCard = inflater.inflate(R.layout.fragment_course_detail_car_list, detailCard, false);
                                TextView placeText = placeCard.findViewById(R.id.detail_car_place_name);
                                TextView distanceText = placeCard.findViewById(R.id.detail_car_distance);
                                TextView timeText = placeCard.findViewById(R.id.detail_car_time);
                                carLocations.add(new CarLocation(
                                        route.startLat,
                                        route.startLon,
                                        route.endLat,
                                        route.endLon
                                ));

                                double distance = route.distance;
                                double km = distance / 1000.0;
                                DecimalFormat df = new DecimalFormat("#.#");
                                distanceText.setText(df.format(km)+"km");
                                placeText.setText(route.start);
                                int time = route.sectionTime;
                                int hourTime = (time / 60 >= 60) ? (time / 60) / 60 : 0;
                                int minTime = (time / 60 >= 60) ? (time % 60) : (time / 60);

                                if (hourTime != 0){
                                    timeText.setText("약 "+hourTime+"시간 "+minTime+"분 소요");
                                }else {
                                    timeText.setText("약 "+minTime+"분 소요");
                                }
                                detailCard.addView(placeCard);
                            }
                            carDays.add(new CarDay(carData.day, carLocations));
                            CourseDetailCarResponse.route lastDetail = carData.routes.get(carData.routes.size() - 1);
                            View endPlaceCard = inflater.inflate(R.layout.fragment_course_detail_car_list, detailCard, false);
                            TextView placeText = endPlaceCard.findViewById(R.id.detail_car_place_name);
                            TextView timeText = endPlaceCard.findViewById(R.id.detail_car_time);
                            placeText.setText(lastDetail.end);
                            timeText.setText("");
                            detailCard.addView(endPlaceCard);
                        }
                        detailListLayout.addView(detailCard);
                    }else {
                        Log.d("courseDetailCar", "데이터 없음");
                    }
                }
            }

            @Override
            public void onFailure(Call<CourseDetailCarResponse> call, Throwable t) {
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
        });
        deleteBtn.setOnClickListener(v -> {
            // 코스 삭제 API
            Call<CourseDeleteResponse> call = apiService.deleteCourse(authorization, courseId);
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
            Call<CourseRatingResponse> call = apiService.rate(authorization, courseRatingRequest);
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
    private void setupMockServer(Runnable onReady) {
        new Thread(() -> {
            try {
                mockServer = new MockWebServer();
                Log.d("mockServer", "Mock 서버 시작");
                mockServer.setDispatcher(new Dispatcher() {
                    @NonNull
                    @Override
                    public MockResponse dispatch(@NonNull RecordedRequest request) {
                        String path = request.getPath();
                        Log.d("mockServer", "요청됨: " + request.getPath());
                        if (path.contains("course/detail/car")) {
                            return new MockResponse()
                                    .setResponseCode(200)
                                    .addHeader("Content-Type", "application/json")
                                    .setBody("{\n" +
                                            "  \"resultCode\": 200,\n" +
                                            "  \"resultMessage\": \"success\",\n" +
                                            "  \"data\": [\n" +
                                            "    {\n" +
                                            "      \"meansTp\": \"자가용\",\n" +
                                            "      \"day\": \"2025-06-01\",\n" +
                                            "      \"routes\": [\n" +
                                            "        {\n" +
                                            "          \"start\": \"첨성대\",\n" +
                                            "          \"end\": \"국립경주박물관\",\n" +
                                            "          \"startLat\": 35.8341,\n" +
                                            "          \"startLon\": 129.217,\n" +
                                            "          \"endLat\": 35.8294,\n" +
                                            "          \"endLon\": 129.2101,\n" +
                                            "          \"distance\": 1109,\n" +
                                            "          \"sectionTime\": 210\n" +
                                            "        },\n" +
                                            "        {\n" +
                                            "          \"start\": \"국립경주박물관\",\n" +
                                            "          \"end\": \"안압지\",\n" +
                                            "          \"startLat\": 35.8294,\n" +
                                            "          \"startLon\": 129.2101,\n" +
                                            "          \"endLat\": 35.8348,\n" +
                                            "          \"endLon\": 129.213,\n" +
                                            "          \"distance\": 1317,\n" +
                                            "          \"sectionTime\": 220\n" +
                                            "        }\n" +
                                            "      ]\n" +
                                            "    },\n" +
                                            "    {\n" +
                                            "      \"meansTp\": \"자가용\",\n" +
                                            "      \"day\": \"2025-06-02\",\n" +
                                            "      \"routes\": [\n" +
                                            "        {\n" +
                                            "          \"start\": \"포석정\",\n" +
                                            "          \"end\": \"오릉\",\n" +
                                            "          \"startLat\": 35.8312,\n" +
                                            "          \"startLon\": 129.219,\n" +
                                            "          \"endLat\": 35.833,\n" +
                                            "          \"endLon\": 129.2145,\n" +
                                            "          \"distance\": 894,\n" +
                                            "          \"sectionTime\": 167\n" +
                                            "        },\n" +
                                            "        {\n" +
                                            "          \"start\": \"오릉\",\n" +
                                            "          \"end\": \"교촌마을\",\n" +
                                            "          \"startLat\": 35.833,\n" +
                                            "          \"startLon\": 129.2145,\n" +
                                            "          \"endLat\": 35.829,\n" +
                                            "          \"endLon\": 129.218,\n" +
                                            "          \"distance\": 2794,\n" +
                                            "          \"sectionTime\": 407\n" +
                                            "        }\n" +
                                            "      ]\n" +
                                            "    }\n" +
                                            "  ]\n" +
                                            "}");
                        } else if (path.contains("/course/delete/{courseId}")) {
                            return new MockResponse()
                                    .setResponseCode(200)
                                    .addHeader("Content-Type", "application/json")
                                    .setBody("{\"resultCode\":200,\"resultMessage\":\"코스 삭제 성공\"}");
                        } else if (path.contains("/rating")) {
                            return new MockResponse()
                                    .setResponseCode(200)
                                    .addHeader("Content-Type", "application/json")
                                    .setBody("{\"resultCode\":200,\"resultMessage\":\"평가 완료\"}");
                        }

                        return new MockResponse().setResponseCode(404);
                    }
                });

                mockServer.start();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(mockServer.url("/"))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                apiService = retrofit.create(ApiService.class);
                onReady.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            apiService = RetrofitClient.getInstance().create(ApiService.class);
//            onReady.run();
        }).start();
    }
}
