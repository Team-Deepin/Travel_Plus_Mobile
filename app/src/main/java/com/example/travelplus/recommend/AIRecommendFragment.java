package com.example.travelplus.recommend;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.util.Log;
import android.util.TypedValue;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.applandeo.materialcalendarview.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AIRecommendFragment extends Fragment {
    boolean dateFlag = true, transportFlag = true, dateCheck = false, transportCheck = false;
    String selectTransport, startDate, endDate;
    ApiService apiService;
    private MockWebServer mockServer;
    String title;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_recommend, container, false);
        setupMockServer();
        CardView cardDate = view.findViewById(R.id.card_trip_date);
        ImageView dateDown = view.findViewById(R.id.ai_date_down);
        CalendarView calendarView = view.findViewById(R.id.ai_date);
        CardView cardTransport = view.findViewById(R.id.card_transport);
        ImageView transportDown = view.findViewById(R.id.ai_transport_down);
        CardView aiCar = view.findViewById(R.id.ai_car);
        CardView aiTransport = view.findViewById(R.id.ai_transport);
        CardView dateSelect = view.findViewById(R.id.ai_date_select);
        CardView transportSelect = view.findViewById(R.id.ai_transport_select);
        TextView dateSelectText = view.findViewById(R.id.ai_date_select_text);
        TextView transportSelectText = view.findViewById(R.id.ai_transport_select_text);
        CardView dateCheckBtn = view.findViewById(R.id.ai_calendar_check_btn);
        View line1 = view.findViewById(R.id.ai_line1);
        View line2 = view.findViewById(R.id.ai_line2);
        ImageView aiBtn = view.findViewById(R.id.ai_btn);
        aiBtn.setEnabled(false);

        Runnable updateAiBtnState = () -> {
            boolean enabled = dateCheck && transportCheck;
            aiBtn.setEnabled(enabled);
            if (enabled) {
                aiBtn.setImageResource(R.drawable.ai_recommend);
            } else {
                aiBtn.setImageResource(R.drawable.ai_recommend_btn_deactivate);
            }
        };

        dateDown.setOnClickListener(view1 -> {
            if (dateFlag){
                int newHeightPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        465,
                        getResources().getDisplayMetrics()
                );
                ViewGroup.LayoutParams params = cardDate.getLayoutParams();
                params.height = newHeightPx;
                cardDate.setLayoutParams(params);
                int color = ContextCompat.getColor(requireContext(), R.color.point);
                cardDate.setCardBackgroundColor(color);
                calendarView.setVisibility(VISIBLE);
                line1.setVisibility(VISIBLE);
                dateCheckBtn.setVisibility(VISIBLE);
                dateFlag = false;
            }else {
                int newHeightPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        50,
                        getResources().getDisplayMetrics()
                );
                ViewGroup.LayoutParams params = cardDate.getLayoutParams();
                params.height = newHeightPx;
                cardDate.setLayoutParams(params);
                int color = ContextCompat.getColor(requireContext(), R.color.login_button);
                cardDate.setCardBackgroundColor(color);
                calendarView.setVisibility(GONE);
                line1.setVisibility(GONE);
                dateCheckBtn.setVisibility(GONE);
                dateFlag = true;
            }
        });
        transportDown.setOnClickListener(view1 -> {
            if (transportFlag){
                int newHeightPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        130,
                        getResources().getDisplayMetrics()
                );
                ViewGroup.LayoutParams params = cardTransport.getLayoutParams();
                params.height = newHeightPx;
                cardTransport.setLayoutParams(params);
                int color = ContextCompat.getColor(requireContext(), R.color.point);
                cardTransport.setCardBackgroundColor(color);
                line2.setVisibility(VISIBLE);
                aiCar.setVisibility(VISIBLE);
                aiTransport.setVisibility(VISIBLE);
                transportFlag = false;
            }else {
                int newHeightPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        50,
                        getResources().getDisplayMetrics()
                );
                ViewGroup.LayoutParams params = cardTransport.getLayoutParams();
                params.height = newHeightPx;
                cardTransport.setLayoutParams(params);
                int color = ContextCompat.getColor(requireContext(), R.color.login_button);
                cardTransport.setCardBackgroundColor(color);
                line2.setVisibility(GONE);
                aiCar.setVisibility(GONE);
                aiTransport.setVisibility(GONE);
                transportFlag = true;
            }
        });

        dateCheckBtn.setOnClickListener(view1 -> {
            List<Calendar> selectedDates = calendarView.getSelectedDates();
            if (!selectedDates.isEmpty()) {
                Calendar start = selectedDates.get(0);
                Calendar end = selectedDates.get(selectedDates.size() - 1);

                SimpleDateFormat fmt = new SimpleDateFormat("MM/dd", Locale.getDefault());
                String text = fmt.format(start.getTime());
                if (!start.equals(end)) {
                    text += " ~ " + fmt.format(end.getTime());
                }
                dateSelectText.setText(text);
                dateSelect.setVisibility(VISIBLE);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                startDate = dateFormat.format(start.getTime());
                endDate = dateFormat.format(end.getTime());
                dateCheck = true;
                updateAiBtnState.run();
            }
            int newHeightPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    50,
                    getResources().getDisplayMetrics()
            );
            ViewGroup.LayoutParams params = cardDate.getLayoutParams();
            params.height = newHeightPx;
            cardDate.setLayoutParams(params);
            int color = ContextCompat.getColor(requireContext(), R.color.login_button);
            cardDate.setCardBackgroundColor(color);
            calendarView.setVisibility(GONE);
            line1.setVisibility(GONE);
            dateCheckBtn.setVisibility(GONE);
            dateFlag = true;
        });
        aiCar.setOnClickListener(view1 -> {
            selectTransport = "자가용";
            transportSelectText.setText(selectTransport);
            transportSelect.setVisibility(VISIBLE);
            transportCheck = true;
            updateAiBtnState.run();
        });
        aiTransport.setOnClickListener(view1 -> {
            selectTransport = "대중교통";
            transportSelectText.setText(selectTransport);
            transportSelect.setVisibility(VISIBLE);
            transportCheck = true;
            updateAiBtnState.run();
        });

        aiBtn.setOnClickListener(view1 -> {
            AIRecommendRequest aiRecommendRequest = new AIRecommendRequest(startDate, endDate, selectTransport);
            Call<AIRecommendResponse> call = apiService.recommend(aiRecommendRequest);
            call.enqueue(new Callback<AIRecommendResponse>() {
                @Override
                public void onResponse(Call<AIRecommendResponse> call, Response<AIRecommendResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        AIRecommendResponse res = response.body();
                        Log.d("recommend",res.resultMessage);
                        if (res.resultCode == 200) {
                            Bundle bundle = new Bundle();
                            bundle.putString("title",title);
                            bundle.putString("date", dateSelectText.getText().toString().trim());
                            bundle.putString("transit",selectTransport);
                            bundle.putSerializable("data",(Serializable) res.data);

                            AIResultFragment resultFragment = new AIResultFragment();
                            resultFragment.setArguments(bundle);
                            ConstraintLayout aiLayout = requireView().findViewById(R.id.ai_layout);
                            aiLayout.setVisibility(GONE);
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.ai_fragment_container, resultFragment)
                                    .commit();
                        }else {
                            Log.d("recommend",res.resultMessage);
                        }
                    }
                }

                @Override
                public void onFailure(Call<AIRecommendResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        });

        return view;
    }
    private void setupMockServer() {
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
                                "      \"courseId\": 101,\n" +
                                "      \"courseDetails\": [\n" +
                                "        {\n" +
                                "          \"area\": \"seoul\",\n" +
                                "          \"places\": [\n" +
                                "            {\n" +
                                "              \"day\": \"2025-06-01\",\n" +
                                "              \"placeName\": \"광장시장\",\n" +
                                "              \"placeLat\": 37.5704,\n" +
                                "              \"placeLon\": 126.991,\n" +
                                "              \"placeAddress\": \"서울 종로구 창경궁로 88\",\n" +
                                "              \"sequence\": 1,\n" +
                                "              \"placeType\": \"전통시장\"\n" +
                                "            }\n" +
                                "          ]\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"area\": \"hongdae\",\n" +
                                "          \"places\": [\n" +
                                "            {\n" +
                                "              \"day\": \"2025-06-02\",\n" +
                                "              \"placeName\": \"홍대 거리\",\n" +
                                "              \"placeLat\": 37.5563,\n" +
                                "              \"placeLon\": 126.9229,\n" +
                                "              \"placeAddress\": \"서울 마포구 와우산로\",\n" +
                                "              \"sequence\": 1,\n" +
                                "              \"placeType\": \"문화\"\n" +
                                "            }\n" +
                                "          ]\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"area\": \"itaewon\",\n" +
                                "          \"places\": [\n" +
                                "            {\n" +
                                "              \"day\": \"2025-06-03\",\n" +
                                "              \"placeName\": \"이태원 거리\",\n" +
                                "              \"placeLat\": 37.5345,\n" +
                                "              \"placeLon\": 126.9941,\n" +
                                "              \"placeAddress\": \"서울 용산구 이태원로\",\n" +
                                "              \"sequence\": 1,\n" +
                                "              \"placeType\": \"음식\"\n" +
                                "            }\n" +
                                "          ]\n" +
                                "        }\n" +
                                "      ]\n" +
                                "    },\n" +
                                "    {\n" +
                                "      \"courseId\": 202,\n" +
                                "      \"courseDetails\": [\n" +
                                "        {\n" +
                                "          \"area\": \"busan\",\n" +
                                "          \"places\": [\n" +
                                "            {\n" +
                                "              \"day\": \"2025-07-01\",\n" +
                                "              \"placeName\": \"해운대\",\n" +
                                "              \"placeLat\": 35.1587,\n" +
                                "              \"placeLon\": 129.1604,\n" +
                                "              \"placeAddress\": \"부산 해운대구\",\n" +
                                "              \"sequence\": 1,\n" +
                                "              \"placeType\": \"해변\"\n" +
                                "            }\n" +
                                "          ]\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"area\": \"gamcheon\",\n" +
                                "          \"places\": [\n" +
                                "            {\n" +
                                "              \"day\": \"2025-07-02\",\n" +
                                "              \"placeName\": \"감천문화마을\",\n" +
                                "              \"placeLat\": 35.0975,\n" +
                                "              \"placeLon\": 129.0108,\n" +
                                "              \"placeAddress\": \"부산 사하구 감내2로\",\n" +
                                "              \"sequence\": 1,\n" +
                                "              \"placeType\": \"문화\"\n" +
                                "            },\n" +
                                "            {\n" +
                                "              \"day\": \"2025-07-02\",\n" +
                                "              \"placeName\": \"testData\",\n" +
                                "              \"placeLat\": 35.0975,\n" +
                                "              \"placeLon\": 129.0108,\n" +
                                "              \"placeAddress\": \"부산 사하구 감내2로\",\n" +
                                "              \"sequence\": 2,\n" +
                                "              \"placeType\": \"문화\"\n" +
                                "            }\n" +
                                "          ]\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"area\": \"nampo\",\n" +
                                "          \"places\": [\n" +
                                "            {\n" +
                                "              \"day\": \"2025-07-03\",\n" +
                                "              \"placeName\": \"남포동 거리\",\n" +
                                "              \"placeLat\": 35.0979,\n" +
                                "              \"placeLon\": 129.0351,\n" +
                                "              \"placeAddress\": \"부산 중구 남포동\",\n" +
                                "              \"sequence\": 1,\n" +
                                "              \"placeType\": \"쇼핑\"\n" +
                                "            }\n" +
                                "          ]\n" +
                                "        }\n" +
                                "      ]\n" +
                                "    },\n" +
                                "    {\n" +
                                "      \"courseId\": 103,\n" +
                                "      \"courseDetails\": [\n" +
                                "        {\n" +
                                "          \"area\": \"seoul\",\n" +
                                "          \"places\": [\n" +
                                "            {\n" +
                                "              \"day\": \"2025-06-01\",\n" +
                                "              \"placeName\": \"광장시장\",\n" +
                                "              \"placeLat\": 37.5704,\n" +
                                "              \"placeLon\": 126.991,\n" +
                                "              \"placeAddress\": \"서울 종로구 창경궁로 88\",\n" +
                                "              \"sequence\": 1,\n" +
                                "              \"placeType\": \"전통시장\"\n" +
                                "            }\n" +
                                "          ]\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"area\": \"hongdae\",\n" +
                                "          \"places\": [\n" +
                                "            {\n" +
                                "              \"day\": \"2025-06-02\",\n" +
                                "              \"placeName\": \"홍대 거리\",\n" +
                                "              \"placeLat\": 37.5563,\n" +
                                "              \"placeLon\": 126.9229,\n" +
                                "              \"placeAddress\": \"서울 마포구 와우산로\",\n" +
                                "              \"sequence\": 1,\n" +
                                "              \"placeType\": \"문화\"\n" +
                                "            }\n" +
                                "          ]\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"area\": \"itaewon\",\n" +
                                "          \"places\": [\n" +
                                "            {\n" +
                                "              \"day\": \"2025-06-03\",\n" +
                                "              \"placeName\": \"이태원 거리\",\n" +
                                "              \"placeLat\": 37.5345,\n" +
                                "              \"placeLon\": 126.9941,\n" +
                                "              \"placeAddress\": \"서울 용산구 이태원로\",\n" +
                                "              \"sequence\": 1,\n" +
                                "              \"placeType\": \"음식\"\n" +
                                "            }\n" +
                                "          ]\n" +
                                "        }\n" +
                                "      ]\n" +
                                "    },\n" +
                                "    {\n" +
                                "      \"courseId\": 105,\n" +
                                "      \"courseDetails\": [\n" +
                                "        {\n" +
                                "          \"area\": \"seoul\",\n" +
                                "          \"places\": [\n" +
                                "            {\n" +
                                "              \"day\": \"2025-06-01\",\n" +
                                "              \"placeName\": \"광장시장\",\n" +
                                "              \"placeLat\": 37.5704,\n" +
                                "              \"placeLon\": 126.991,\n" +
                                "              \"placeAddress\": \"서울 종로구 창경궁로 88\",\n" +
                                "              \"sequence\": 1,\n" +
                                "              \"placeType\": \"전통시장\"\n" +
                                "            }\n" +
                                "          ]\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"area\": \"hongdae\",\n" +
                                "          \"places\": [\n" +
                                "            {\n" +
                                "              \"day\": \"2025-06-02\",\n" +
                                "              \"placeName\": \"홍대 거리\",\n" +
                                "              \"placeLat\": 37.5563,\n" +
                                "              \"placeLon\": 126.9229,\n" +
                                "              \"placeAddress\": \"서울 마포구 와우산로\",\n" +
                                "              \"sequence\": 1,\n" +
                                "              \"placeType\": \"문화\"\n" +
                                "            }\n" +
                                "          ]\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"area\": \"itaewon\",\n" +
                                "          \"places\": [\n" +
                                "            {\n" +
                                "              \"day\": \"2025-06-03\",\n" +
                                "              \"placeName\": \"이태원 거리\",\n" +
                                "              \"placeLat\": 37.5345,\n" +
                                "              \"placeLon\": 126.9941,\n" +
                                "              \"placeAddress\": \"서울 용산구 이태원로\",\n" +
                                "              \"sequence\": 1,\n" +
                                "              \"placeType\": \"음식\"\n" +
                                "            }\n" +
                                "          ]\n" +
                                "        }\n" +
                                "      ]\n" +
                                "    }\n" +
                                "  ]\n" +
                                "}"));


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
