package com.example.travelplus.notice;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.SharedPreferences;
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
import com.example.travelplus.network.RetrofitClient;
import com.facebook.shimmer.ShimmerFrameLayout;

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
    int currentPage = 1, pageSize = 7, totalCount = 0;
    ApiService apiService;
    ShimmerFrameLayout noticeSkeleton;

    @Override
    public void onResume() {
        super.onResume();
        if (apiService != null) {
            loadNotices(currentPage-1);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        paginationContainer = view.findViewById(R.id.pagination_container);
        noNoticeContainer = view.findViewById(R.id.notice_no_list);
        noticeRecyclerView = view.findViewById(R.id.notice_list);
        apiService = RetrofitClient.getApiInstance(requireContext()).create(ApiService.class);
        noticeSkeleton = view.findViewById(R.id.notice_skeleton);
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

        return view;
    }
    private void loadNotices(int page) {
        noticeSkeleton.setVisibility(VISIBLE);
        noticeSkeleton.startShimmer();
        noticeRecyclerView.setVisibility(GONE);
        apiService.getNotices(page, pageSize).enqueue(new Callback<NoticeResponse>() {
            @Override
            public void onResponse(Call<NoticeResponse> call, Response<NoticeResponse> response) {
                noticeSkeleton.stopShimmer();
                noticeSkeleton.setVisibility(GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<NoticeResponse.Notice> notices = response.body().data.content;
                    totalCount = response.body().data.totalElements;
                    if (notices != null && notices.isEmpty()) {
                        noticeRecyclerView.setVisibility(GONE);
                        noNoticeContainer.setVisibility(View.VISIBLE);
                    } else {
                        adapter.setItems(notices);
                        setupPagination((int) Math.ceil(totalCount / (float) pageSize));
                        noticeRecyclerView.setVisibility(View.VISIBLE);
                        noNoticeContainer.setVisibility(GONE);
                    }
                }else {
                    noticeRecyclerView.setVisibility(GONE);
                    noNoticeContainer.setVisibility(View.VISIBLE);
                    Log.d("notice", "연결 실패");
                }
            }

            @Override
            public void onFailure(Call<NoticeResponse> call, Throwable t) {
                noticeSkeleton.stopShimmer();
                noticeSkeleton.setVisibility(GONE);
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
                int sendPage = currentPage -1;
                loadNotices(sendPage);
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
                int sendPage = currentPage -1;
                loadNotices(sendPage);
            });
            paginationContainer.addView(pageBtn);
        }

        if (endPage < totalPages) {
            TextView nextBtn = createPageButton(" ▶");
            nextBtn.setOnClickListener(v -> {
                currentPage = endPage + 1;
                int sendPage = currentPage -1;
                loadNotices(sendPage);
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
}
