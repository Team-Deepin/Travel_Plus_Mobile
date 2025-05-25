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
import com.example.travelplus.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NoticeDetailFragment extends Fragment {
    int id;
    TextView detailTitle, detailDate, detailContent;
    ApiService apiService;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt("noticeId");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice_detail, container, false);
        detailTitle = view.findViewById(R.id.notice_detail_title);
        detailDate = view.findViewById(R.id.notice_detail_date);
        detailContent = view.findViewById(R.id.notice_detail_content);

        apiService = RetrofitClient.getApiInstance(requireContext()).create(ApiService.class);
        loadNoticeDetail(id);

        return view;
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
                    Log.e("noticeDetail ",response.message());
                }
            }

            @Override
            public void onFailure(Call<NoticeDetailResponse> call, Throwable t) {
                Log.e("NoticeDetail", "상세 불러오기 실패", t);
            }
        });
    }
}
