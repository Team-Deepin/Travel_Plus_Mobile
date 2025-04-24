package com.example.travelplus.fragment;

import static android.content.Intent.getIntent;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.travelplus.CourseList;
import com.example.travelplus.IsFirstResponse;
import com.example.travelplus.MainActivity;
import com.example.travelplus.OnboardingActivity;
import com.example.travelplus.R;
import com.example.travelplus.WeatherList;
import com.example.travelplus.WeatherResponse;
import com.example.travelplus.network.ApiService;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainFragment extends Fragment {
    Spinner loactionList;
    String apiKey = "6340120faacb6462dae3d3b224bf7e37";
    TextView todayTemp, tomorrowTemp, TDATTemp, homeTitle, homeDuration, homeMeansTP;
    ImageView todayWeather, tomorrowWeather, TDATWeather;
    List<WeatherList> weatherListFromDB = Arrays.asList(
            new WeatherList("서울", new Date(), 23.7f),
            new WeatherList("부산", new Date(), 22.3f)
    );
    private MockWebServer mockServer;
    ApiService apiService;
    CardView homeList;
    ConstraintLayout weatherList;
    LinearLayout homeWeatherList;
    private static final String TAG = "MainFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_main,container,false);
        homeList = view.findViewById(R.id.home_list);
        weatherList = view.findViewById(R.id.weather_list);
        HorizontalScrollView homeScroll = view.findViewById(R.id.home_scroll);
        homeWeatherList = view.findViewById(R.id.home_weather_list);
        setupMockServer(inflater);
        loactionList = view.findViewById(R.id.weather_location);
        todayTemp = view.findViewById(R.id.today_temperature);
        tomorrowTemp = view.findViewById(R.id.tomorrow_temperature);
        TDATTemp = view.findViewById(R.id.the_day_after_tomorrow_temperature);
        todayWeather = view.findViewById(R.id.today_weather);
        tomorrowWeather = view.findViewById(R.id.tomorrow_weather);
        TDATWeather = view.findViewById(R.id.the_day_after_tomorrow_weather);
        homeTitle = view.findViewById(R.id.home_title);
        homeDuration = view.findViewById(R.id.home_duration);
        homeMeansTP = view.findViewById(R.id.home_meansTP);

        String[] items = {"서울", "부산", "수원", "인천", "대구", "대전", "광주", "울산", "제주"};
        Map<String, String> weatherLocation = new LinkedHashMap<>();
        weatherLocation.put("서울","Seoul");
        weatherLocation.put("부산","Busan");
        weatherLocation.put("수원","Suwon");
        weatherLocation.put("인천","Incheon");
        weatherLocation.put("대구","Daegu");
        weatherLocation.put("대전","Daejeon");
        weatherLocation.put("광주","Gwangju");
        weatherLocation.put("울산","Ulsan");
        weatherLocation.put("제주","Jeju");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_list, items);
        adapter.setDropDownViewResource(R.layout.dropdown_list);
        loactionList.setAdapter(adapter);
        loactionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedView, int position, long id) {
                String selectedKorean = items[position];
                String selectedEnglish = weatherLocation.get(selectedKorean);

                String city = selectedEnglish + ",KR";
                String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city
                        + "&appid=" + apiKey + "&units=metric&lang=kr";

                new GetWeatherTask().execute(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String url = "https://api.openweathermap.org/data/2.5/forecast?q=Seoul"
                        + "&appid=" + apiKey + "&units=metric&lang=kr";
                new GetWeatherTask().execute(url);
            }
        });

        return view;
    }
    private class GetWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
            } catch (Exception e) {
                Log.e("Weather", "에러: " + e.getMessage());
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String json) {
            if (json == null || json.isEmpty()) {
                Toast.makeText(getContext(), "날씨 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // JSON 파싱
                Gson gson = new Gson();
                WeatherResponse weather = gson.fromJson(json, WeatherResponse.class);

                String tomorrow = getTargetDate(1);
                String dayAfter = getTargetDate(2);

                boolean todaySet = false;

                for (WeatherResponse.ForecastItem item : weather.list) {
                    float temp = item.main.temp;
                    String formattedTemp = String.format(Locale.getDefault(), "%.1f", temp);
                    if (!todaySet && item.dt_txt.contains(getTargetDate(0))) {
                        todayTemp.setText(formattedTemp + "°C");
                        setWeatherImage(todayWeather, item.weather.get(0).main);
                        todaySet = true;
                    }

                    if (item.dt_txt.contains(tomorrow + " 12:00:00")) {
                        tomorrowTemp.setText(formattedTemp + "°C");
                        setWeatherImage(tomorrowWeather, item.weather.get(0).main);
                    }

                    if (item.dt_txt.contains(dayAfter + " 12:00:00")) {
                        TDATTemp.setText(formattedTemp+ "°C");
                        setWeatherImage(TDATWeather, item.weather.get(0).main);
                    }
                }

            } catch (Exception e) {
                Log.e("Weather", "파싱 오류: " + e.getMessage());
            }
        }
    }
    private String getTargetDate(int dayOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayOffset);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
    private void setWeatherImage(ImageView view, String mainWeather) {
        switch (mainWeather) {
            case "Rain":
            case "Drizzle":
            case "Thunderstorm":
                view.setImageResource(R.drawable.rainy);
                break;
            case "Clear":
                view.setImageResource(R.drawable.sunny);
                break;
            case "Clouds":
                view.setImageResource(R.drawable.cloudy);
                break;
            case "Snow":
                view.setImageResource(R.drawable.snowy);
                break;
            default:
                view.setImageResource(R.drawable.sunny);
        }
    }
    private void checkIsFirst(LayoutInflater inflater) {
        Call<IsFirstResponse> call = apiService.getIsFirst();
        call.enqueue(new Callback<IsFirstResponse>() {
            @Override
            public void onResponse(Call<IsFirstResponse> call, Response<IsFirstResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isFirst = response.body().isFirst;
                    boolean isTraveling = false;
                    Date today = new Date();
                    String startDateStr = response.body().startDate;
                    String endDateStr = response.body().endDate;
                    String duration="";
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        Date startDate = dateFormat.parse(startDateStr);
                        Date endDate = dateFormat.parse(endDateStr);

                        long diffInMillis = endDate.getTime() - startDate.getTime();
                        long diffTodayStart = today.getTime() - startDate.getTime();
                        long diffTodayEnd = endDate.getTime() - today.getTime();
                        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
                        if (diffInDays == 0) {
                            duration = "당일치기";
                        } else if (diffInDays == 1) {
                            duration = "1박 2일";
                        }
                        else {
                            duration = diffInDays + "박 " + (diffInDays + 1) + "일";
                        }
                        if (diffTodayStart >= 0 && diffTodayEnd >= 0) {
                            isTraveling = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "isFirst : " + isFirst);
                    if (isFirst) {
                        Intent onboardingIntent = new Intent(getContext(), OnboardingActivity.class);
                        startActivity(onboardingIntent);
                        requireActivity().finish();
                    }else {
                        if(isTraveling){
                            homeList.setVisibility(VISIBLE);
                            weatherList.setVisibility(GONE);
                            loactionList.setVisibility(GONE);
                            homeTitle.setText(response.body().title);
                            homeDuration.setText(duration+",");
                            homeMeansTP.setText(response.body().meansTp);
                            homeWeatherList.removeAllViews();

                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
                            for (WeatherList weather : weatherListFromDB) {
                                View card = inflater.inflate(R.layout.fragment_weather_list, homeWeatherList, false);

                                TextView locations = card.findViewById(R.id.home_location);
                                TextView date = card.findViewById(R.id.home_date);
                                TextView temp = card.findViewById(R.id.home_temperature);
                                ImageView weatherImage = card.findViewById(R.id.home_weather_image);

                                locations.setText(weather.location);
                                date.setText(dateFormat.format(weather.date));
                                temp.setText(String.format(Locale.getDefault(), "%.1f°C", weather.temperature));
                                weatherImage.setImageResource(R.drawable.sunny); // 예시로 sunny 고정

                                homeWeatherList.addView(card);
                            }
                        }else{
                            homeList.setVisibility(GONE);
                            weatherList.setVisibility(VISIBLE);
                            loactionList.setVisibility(VISIBLE);
                            homeWeatherList.setVisibility(GONE);
                        }
                    }
                } else {
                    Log.e(TAG, "Response failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<IsFirstResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }
    private void setupMockServer(LayoutInflater inflater) {
        new Thread(() -> {
            try {
                mockServer = new MockWebServer();

                // 응답 설정 (isFirst = true로 테스트)
                mockServer.enqueue(new MockResponse()
                        .setResponseCode(200)
                        .setBody("{\"isFirst\": false,\"title\":\"제주도\",\"area\":\"제주도\",\"startDate\":\"2025-04-24\"," +
                                "\"endDate\":\"2025-04-26\",\"meansTp\":\"자가용\"}")
                        .addHeader("Content-Type", "application/json"));

                mockServer.start();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(mockServer.url("/")) // mock 서버 URL 사용
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                apiService = retrofit.create(ApiService.class);

                // mock 서버 준비되면 사용자 ID로 테스트 시작
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkIsFirst(inflater);
                    }
                });

            } catch (IOException e) {
                Log.e(TAG, "MockServer setup failed: " + e.getMessage());
            }
        }).start();
    }

}
