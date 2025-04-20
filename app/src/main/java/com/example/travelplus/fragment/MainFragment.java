package com.example.travelplus.fragment;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travelplus.R;
import com.example.travelplus.WeatherResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class MainFragment extends Fragment {
    Spinner weather;
    String apiKey = "6340120faacb6462dae3d3b224bf7e37";
    TextView todayTemp, tomorrowTemp, TDATTemp;
    ImageView todayWeather, tomorrowWeather, TDATWeather;
    String mainWeather;
    float temp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_main,container,false);

        weather = view.findViewById(R.id.weather_location);
        todayTemp = view.findViewById(R.id.today_temperature);
        tomorrowTemp = view.findViewById(R.id.tomorrow_temperature);
        TDATTemp = view.findViewById(R.id.the_day_after_tomorrow_temperature);
        todayWeather = view.findViewById(R.id.today_weather);
        tomorrowWeather = view.findViewById(R.id.tomorrow_weather);
        TDATWeather = view.findViewById(R.id.the_day_after_tomorrow_weather);
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
        weather.setAdapter(adapter);
        weather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

//                temp = weather.main.temp;
//                mainWeather = weather.weather.get(0).main;
                boolean todaySet = false;

                for (WeatherResponse.ForecastItem item : weather.list) {
                    if (!todaySet && item.dt_txt.contains(getTargetDate(0))) {
                        todayTemp.setText(item.main.temp + "°C");
                        setWeatherImage(todayWeather, item.weather.get(0).main);
                        todaySet = true;
                    }

                    if (item.dt_txt.contains(tomorrow + " 12:00:00")) {
                        tomorrowTemp.setText(item.main.temp + "°C");
                        setWeatherImage(tomorrowWeather, item.weather.get(0).main);
                    }

                    if (item.dt_txt.contains(dayAfter + " 12:00:00")) {
                        TDATTemp.setText(item.main.temp + "°C");
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


}
