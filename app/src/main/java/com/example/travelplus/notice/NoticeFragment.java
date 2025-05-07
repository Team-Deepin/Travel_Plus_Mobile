package com.example.travelplus.notice;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;

import java.io.IOException;
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

public class NoticeFragment extends Fragment {
    LinearLayout paginationContainer;
    ConstraintLayout noNoticeContainer;
    RecyclerView noticeRecyclerView;
    NoticeAdapter adapter;
    int currentPage = 1;
    int pageSize = 7;
    int totalCount = 0;
    ApiService apiService;
    private MockWebServer mockServer;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        paginationContainer = view.findViewById(R.id.pagination_container);
        noNoticeContainer = view.findViewById(R.id.notice_no_list);
        noticeRecyclerView = view.findViewById(R.id.notice_list);


        adapter = new NoticeAdapter();
        noticeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noticeRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(notice -> {
            ConstraintLayout noticeLayout = requireView().findViewById(R.id.notice_layout);
            noticeLayout.setVisibility(GONE);
            Bundle bundle = new Bundle();
            bundle.putInt("noticeId", notice.noticeId);

            NoticeDetailFragment detailFragment = new NoticeDetailFragment();
            detailFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.notice_fragment_container, detailFragment) // 부모 레이아웃 id
                    .addToBackStack(null)
                    .commit();
        });
        requireActivity().getSupportFragmentManager()
                .addOnBackStackChangedListener(() -> {
                    if (isAdded()) {
                        if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() == 1) {
                            ConstraintLayout noticeLayout = requireView().findViewById(R.id.notice_layout);
                            noticeLayout.setVisibility(VISIBLE);
                        }
                    }
                });
        setupMockServer();

        return view;
    }
    private void loadNotices(int page) {
        apiService.getNotices(page, pageSize).enqueue(new Callback<NoticeResponse>() {
            @Override
            public void onResponse(Call<NoticeResponse> call, Response<NoticeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NoticeResponse.Notice> notices = response.body().data.notices;
                    totalCount = response.body().data.totalCount;

                    if (notices.isEmpty()) {
                        noticeRecyclerView.setVisibility(GONE);
                        noNoticeContainer.setVisibility(View.VISIBLE);
                    } else {
                        adapter.setItems(notices);
                        setupPagination((int) Math.ceil(totalCount / (float) pageSize));
                        noticeRecyclerView.setVisibility(View.VISIBLE);
                        noNoticeContainer.setVisibility(GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<NoticeResponse> call, Throwable t) {
                Log.e("notice", "불러오기 실패", t);
            }
        });
    }
    private void setupPagination(int totalPages) {
        paginationContainer.removeAllViews();
        int pageGroupSize = 10;
        int startPage = ((currentPage - 1) / pageGroupSize) * pageGroupSize + 1;
        int endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

        if (startPage > 1) {
            TextView prevBtn = createPageButton("◀ ");
            prevBtn.setOnClickListener(v -> {
                currentPage = startPage - 1;
                loadNotices(currentPage);
            });
            paginationContainer.addView(prevBtn);
        }

        for (int i = startPage; i <= endPage; i++) {
            final int page = i;
            TextView pageBtn = createPageButton(String.valueOf(page));
            pageBtn.setTextColor(page == currentPage ? Color.BLACK : Color.GRAY);
            pageBtn.setTypeface(null, page == currentPage ? Typeface.BOLD : Typeface.NORMAL);
            pageBtn.setOnClickListener(v -> {
                currentPage = page;
                loadNotices(currentPage);
            });
            paginationContainer.addView(pageBtn);
        }

        if (endPage < totalPages) {
            TextView nextBtn = createPageButton(" ▶");
            nextBtn.setOnClickListener(v -> {
                currentPage = endPage + 1;
                loadNotices(currentPage);
            });
            paginationContainer.addView(nextBtn);
        }
    }
    private TextView createPageButton(String text) {
        TextView btn = new TextView(getContext());
        btn.setText(text);
        btn.setTextSize(16);
        btn.setPadding(24, 12, 24, 12);
        btn.setTextColor(Color.GRAY);
        return btn;
    }
    private void setupMockServer() {
        new Thread(() -> {
            try {
                mockServer = new MockWebServer();

                // JSON 데이터를 페이지별로 미리 생성
                List<String> noticesJson = new ArrayList<>();
                for (int i = 1; i <= 20; i++) {
                    noticesJson.add("{\"noticeId\":" + i + ",\"title\":\"공지사항 " + i + "\",\"date\":\"2025-04-" + (i < 10 ? "0" + i : i) + "\"}");
                }

                // Dispatcher 설정
                mockServer.setDispatcher(new Dispatcher() {
                    @Override
                    public MockResponse dispatch(RecordedRequest request) {
                        String path = request.getPath(); // 예: /edit/notice?page=2&size=7
                        int page = 1;

                        try {
                            String[] query = path.split("\\?")[1].split("&");
                            for (String q : query) {
                                if (q.startsWith("page=")) {
                                    page = Integer.parseInt(q.split("=")[1]);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        int from = (page - 1) * 7;
                        int to = Math.min(from + 7, noticesJson.size());
                        String body = "{"
                                + "\"resultCode\": 200,"
                                + "\"resultMessage\": \"Success\","
                                + "\"data\": {"
                                + "\"totalCount\": 20,"
                                + "\"notices\": [" + String.join(",", noticesJson.subList(from, to)) + "]"
                                + "}"
                                + "}";

                        return new MockResponse().setResponseCode(200).setBody(body);
                    }
                });

                mockServer.start();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(mockServer.url("/"))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                apiService = retrofit.create(ApiService.class);

                requireActivity().runOnUiThread(() -> loadNotices(currentPage));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
