package com.example.travelplus.notice;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;

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

public class NoticeDetailFragment extends Fragment {
    int id;
    TextView detailTitle, detailDate, detailContent;
    ApiService apiService;
    private MockWebServer mockServer;
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = (int) getArguments().get("noticeId");
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice_detail, container, false);
        detailTitle = view.findViewById(R.id.notice_detail_title);
        detailDate = view.findViewById(R.id.notice_detail_date);
        detailContent = view.findViewById(R.id.notice_detail_content);

        setupMockServer();

        return view;
    }
    private void setupMockServer() {
        new Thread(() -> {
            try {
                mockServer = new MockWebServer();

                mockServer.setDispatcher(new Dispatcher() {
                    @Override
                    public MockResponse dispatch(RecordedRequest request) {
                        String path = request.getPath(); // 예: /edit/notice/3
                        if (path != null && path.startsWith("/edit/notice/")) {
                            try {
                                String[] parts = path.split("/");
                                int noticeId = Integer.parseInt(parts[parts.length - 1]);

                                String body = "{"
                                        + "\"resultCode\": 200,"
                                        + "\"resultMessage\": \"Success\","
                                        + "\"data\": {"
                                        + "\"title\": \"공지사항 " + noticeId + "번\","
                                        + "\"content\": \"공지사항 " + noticeId + "번 내용입니다.\","
                                        + "\"date\": \"2025-04-" + (noticeId < 10 ? "0" + noticeId : noticeId) + "T19:12:45.382\""
                                        + "}"
                                        + "}";

                                return new MockResponse().setResponseCode(200).setBody(body);
                            } catch (Exception e) {
                                return new MockResponse().setResponseCode(400).setBody("{\"resultMessage\":\"Invalid noticeId\"}");
                            }
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

                requireActivity().runOnUiThread(() -> loadNoticeDetail(id));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void loadNoticeDetail(int noticeId) {
        apiService.getNoticeDetail(noticeId).enqueue(new Callback<NoticeDetailResponse>() {
            @Override
            public void onResponse(Call<NoticeDetailResponse> call, Response<NoticeDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NoticeDetailResponse res = response.body();
                    NoticeDetailResponse.Data data = res.data;
                    detailTitle.setText(data.title);
                    detailDate.setText(data.date);
                    detailContent.setText(data.content);
                }else {
                    Log.d("noticeDetail","불러오기 실패");
                }
            }

            @Override
            public void onFailure(Call<NoticeDetailResponse> call, Throwable t) {
                Log.e("NoticeDetail", "상세 불러오기 실패", t);
            }
        });
    }
}
