package com.example.travelplus.home;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.travelplus.onboarding.OnboardingActivity;
import com.example.travelplus.R;
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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    Spinner locationList;
    private String apiKey = "6340120faacb6462dae3d3b224bf7e37";
    TextView todayTemp, tomorrowTemp, TDATTemp, homeTitle, homeDuration, homeMeansTP;
    ImageView todayWeather, tomorrowWeather, TDATWeather;
    private MockWebServer mockServer;
    ApiService apiService;
    CardView homeList;
    ConstraintLayout weatherList;
    LinearLayout homeWeatherList;
    HorizontalScrollView homeScroll;
    Map<String, String> weatherLocation;
    String startDateGlobal, areaGlobal, endDateGlobal, authorization;
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = requireActivity().getSharedPreferences("userPrefs", MODE_PRIVATE);
        authorization = prefs.getString("authorization", null);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_main,container,false);
        setupMockServer(inflater);
        homeList = view.findViewById(R.id.home_list);
        homeScroll = view.findViewById(R.id.home_scroll);
        weatherList = view.findViewById(R.id.weather_list);
        homeWeatherList = view.findViewById(R.id.home_weather_list);
        locationList = view.findViewById(R.id.weather_location);
        todayTemp = view.findViewById(R.id.today_temperature);
        tomorrowTemp = view.findViewById(R.id.tomorrow_temperature);
        TDATTemp = view.findViewById(R.id.the_day_after_tomorrow_temperature);
        todayWeather = view.findViewById(R.id.today_weather);
        tomorrowWeather = view.findViewById(R.id.tomorrow_weather);
        TDATWeather = view.findViewById(R.id.the_day_after_tomorrow_weather);
        homeTitle = view.findViewById(R.id.home_title);
        homeDuration = view.findViewById(R.id.home_duration);
        homeMeansTP = view.findViewById(R.id.home_meansTP);

        String[] items = {"서울", "경기도", "강원도", "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주도"};
        weatherLocation = new LinkedHashMap<>();
        weatherLocation.put("서울","Seoul");
        weatherLocation.put("경기도","Suwon");
        weatherLocation.put("강원도","Chuncheon");
        weatherLocation.put("충청북도","Cheongju");
        weatherLocation.put("충청남도","Cheonan");
        weatherLocation.put("전라북도","Jeonju");
        weatherLocation.put("전라남도","Gwangju");
        weatherLocation.put("경상북도","Pohang");
        weatherLocation.put("경상남도","Changwon");
        weatherLocation.put("제주도","Jeju");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_list, items);
        adapter.setDropDownViewResource(R.layout.dropdown_list);
        locationList.setAdapter(adapter);
        locationList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedView, int position, long id) {
                String selectedKorean = items[position];
                String selectedEnglish = weatherLocation.get(selectedKorean);

                String city = selectedEnglish + ",KR";
                String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city
                        + "&appid=" + apiKey + "&units=metric&lang=kr";

                new GetWeatherNoCourse().execute(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String url = "https://api.openweathermap.org/data/2.5/forecast?q=Seoul"
                        + "&appid=" + apiKey + "&units=metric&lang=kr";
                new GetWeatherNoCourse().execute(url);
            }
        });

        return view;
    }
    private class GetWeatherNoCourse extends AsyncTask<String, Void, String> {
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
    private class GetWeatherCourse extends AsyncTask<String, Void, String> {
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
                Gson gson = new Gson();
                WeatherResponse weather = gson.fromJson(json, WeatherResponse.class);

                LayoutInflater inflater = LayoutInflater.from(getContext());
                SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                SimpleDateFormat kstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                kstFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());

                homeWeatherList.removeAllViews();
                for (WeatherResponse.ForecastItem item : weather.list) {
                    Log.d("예보전체", item.dt_txt);
                    Log.d("온도", String.valueOf(item.main.temp));
                }
                List<String> targetDates = getDateRange(startDateGlobal, endDateGlobal);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                Date today = cal.getTime();

                for (String target : targetDates) {
                    Date targetDate = sdf.parse(target);
                    if (targetDate.before(today)) {
                        continue;
                    }
                    WeatherResponse.ForecastItem matchedItem = null;

                    for (WeatherResponse.ForecastItem item : weather.list) {
                        Date utcDate = utcFormat.parse(item.dt_txt);
                        String kstString = kstFormat.format(utcDate);
                        Date kstDate = kstFormat.parse(kstString);
                        String kstDateOnly = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(kstDate);
                        if (kstDateOnly.equals(target)) {
                            matchedItem = item;
                            break;
                        }
                    }

                    View card = inflater.inflate(R.layout.fragment_weather_list, homeWeatherList, false);

                    TextView location = card.findViewById(R.id.home_location);
                    TextView date = card.findViewById(R.id.home_date);
                    TextView temp = card.findViewById(R.id.home_temperature);
                    ImageView weatherImage = card.findViewById(R.id.home_weather_image);

                    location.setText(areaGlobal);

                    if (matchedItem != null) {
                        Date matchedUTC = utcFormat.parse(matchedItem.dt_txt);
                        Date kstDate = new Date(matchedUTC.getTime() + TimeZone.getTimeZone("Asia/Seoul").getRawOffset());
                        date.setText(outputDateFormat.format(kstDate));

                        Log.d("날짜",outputDateFormat.format(kstDate));

                        temp.setText(String.format(Locale.getDefault(), "%.1f°C", matchedItem.main.temp));
                        setWeatherImage(weatherImage, matchedItem.weather.get(0).main);
                    } else {
                        date.setText(new SimpleDateFormat("MM/dd").format(targetDate));
                        temp.setText("예보 없음");
                        weatherImage.setImageResource(R.drawable.no_image);
                    }

                    homeWeatherList.addView(card);
//                    for (WeatherResponse.ForecastItem item : weather.list) {
//                        if (item.dt_txt.startsWith(target)) {
//                            View card = inflater.inflate(R.layout.fragment_weather_list, homeWeatherList, false);
//
//                            TextView location = card.findViewById(R.id.home_location);
//                            TextView date = card.findViewById(R.id.home_date);
//                            TextView temp = card.findViewById(R.id.home_temperature);
//                            ImageView weatherImage = card.findViewById(R.id.home_weather_image);
//
//                            location.setText(areaGlobal);
//                            date.setText(outputDateFormat.format(inputFormat.parse(item.dt_txt)));
//                            temp.setText(String.format(Locale.getDefault(), "%.1f°C", item.main.temp));
//                            setWeatherImage(weatherImage, item.weather.get(0).main);
//
//                            homeWeatherList.addView(card);
//                            found = true;
//                            break;
//                        }
//                    }
//                    // 예보가 없을 경우
//                    if (!found) {
//                        View card = inflater.inflate(R.layout.fragment_weather_list, homeWeatherList, false);
//
//                        TextView location = card.findViewById(R.id.home_location);
//                        TextView date = card.findViewById(R.id.home_date);
//                        TextView temp = card.findViewById(R.id.home_temperature);
//                        ImageView weatherImage = card.findViewById(R.id.home_weather_image);
//
//                        location.setText(areaGlobal);
//                        SimpleDateFormat display = new SimpleDateFormat("MM/dd");
//                        String displayDate = display.format(sdf.parse(target));
//                        date.setText(displayDate);
//                        temp.setText("예보 없음");
//                        weatherImage.setImageResource(R.drawable.no_image);
//
//                        homeWeatherList.addView(card);
//                    }
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
    private List<String> getDateRange(String start, String end) {
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            while (!calendar.getTime().after(endDate)) {
                dateList.add(sdf.format(calendar.getTime()));
                calendar.add(Calendar.DATE, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateList;
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
        Call<HomeResponse> call = apiService.home(authorization);
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HomeResponse res = response.body();
                    Log.d("home",res.resultMessage);
                    if(res.resultCode == 200){
                        boolean isFirst = res.data.isFirst;
                        boolean isTraveling = false;
                        Date today = new Date();
                        startDateGlobal= res.data.course.get(0).startDate;
                        endDateGlobal = res.data.course.get(0).endDate;
                        String duration="";
                        areaGlobal = res.data.course.get(0).area;
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                            Date startDate = dateFormat.parse(startDateGlobal);
                            Date endDate = dateFormat.parse(endDateGlobal);

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
                        Log.d("home", "isFirst : " + isFirst);
                        if (isFirst) {
                            Intent onboardingIntent = new Intent(getContext(), OnboardingActivity.class);
                            startActivity(onboardingIntent);
                            requireActivity().finish();
                        }else {
                            if(isTraveling){
                                homeList.setVisibility(VISIBLE);
                                weatherList.setVisibility(GONE);
                                locationList.setVisibility(GONE);
                                homeScroll.setVisibility(VISIBLE);
                                homeTitle.setText(res.data.course.get(0).title);
                                homeDuration.setText(duration+",");
                                homeMeansTP.setText(res.data.course.get(0).meansTp);
                                homeWeatherList.removeAllViews();

                                String selectedEnglish = weatherLocation.get(areaGlobal);
                                String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + selectedEnglish
                                        + "&appid=" + apiKey + "&units=metric&lang=kr";

                                new GetWeatherCourse().execute(url);
                            }else{
                                weatherList.setVisibility(VISIBLE);
                                locationList.setVisibility(VISIBLE);
                                homeList.setVisibility(GONE);
                                homeWeatherList.setVisibility(GONE);
                                homeScroll.setVisibility(GONE);
                            }
                        }
                    }else {
                        Log.d("home", "Result Code: " + res.resultCode + ", Result Message: " + res.resultMessage);
                    }
                } else {
                    Log.e("home", "Response failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Log.e("home", "API call failed: " + t);
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
                        .setBody("{"
                                + "\"resultCode\":200,"
                                + "\"resultMessage\":\"성공\","
                                + "\"data\":{"
                                + "\"isFirst\":false,"
                                + "\"course\":["
                                + "{"
                                + "\"courseId\":101,"
                                + "\"title\":\"제주도\","
                                + "\"area\":\"제주도\","
                                + "\"startDate\":\"2025-05-10\","
                                + "\"endDate\":\"2025-05-12\","
                                + "\"meansTp\":\"자가용\""
                                + "}"
                                + "]"
                                + "}"
                                + "}")
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
                Log.e("home", "MockServer setup failed: " + e.getMessage());
            }
        }).start();
    }

}
