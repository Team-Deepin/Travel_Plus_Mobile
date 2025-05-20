package com.example.travelplus.inquiry;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InquiryFragment extends Fragment {
    ConstraintLayout noListContainer;
    ScrollView inquiryScroll;
    LinearLayout inquiryList;
    ApiService apiService;
    ShimmerFrameLayout inquirySkeleton;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (apiService != null) {
            inquiryList.removeAllViews();
            inquiryLists(LayoutInflater.from(requireContext()));
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inquiry, container, false);
        apiService = RetrofitClient.getApiInstance(requireContext()).create(ApiService.class);
        noListContainer = view.findViewById(R.id.inquiry_no_list_container);
        ImageView inquiryBtn = view.findViewById(R.id.inquiry_btn);
        inquiryScroll = view.findViewById(R.id.inquiry_scroll);
        inquiryList = view.findViewById(R.id.inquiry_list);
        inquirySkeleton = view.findViewById(R.id.inquiry_skeleton);
        inquiryBtn.setOnClickListener(view1 -> {
            // 문의하기 이동
            InquireFragment inquireFragment = new InquireFragment();
            ConstraintLayout inquiryLayout = requireView().findViewById(R.id.inquiry_layout);
            inquiryLayout.setVisibility(GONE);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.inquiry_fragment_container, inquireFragment)
                    .addToBackStack(null)
                    .commit();
        });
        
        requireActivity().getSupportFragmentManager()
                .addOnBackStackChangedListener(() -> {
                    if (isAdded()) {
                        if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() == 1) {
                            ConstraintLayout inquiryLayout = requireView().findViewById(R.id.inquiry_layout);
                            inquiryLayout.setVisibility(VISIBLE);
                        }
                    }
                });

        return view;
    }
    private void inquiryLists(LayoutInflater inflater) {
        inquirySkeleton.setVisibility(VISIBLE);
        inquirySkeleton.startShimmer();
        inquiryScroll.setVisibility(GONE);
        noListContainer.setVisibility(GONE);
        Call<InquiryResponse> call = apiService.inquiry();
        call.enqueue(new Callback<InquiryResponse>() {
            @Override
            public void onResponse(Call<InquiryResponse> call, Response<InquiryResponse> response) {
                inquirySkeleton.stopShimmer();
                inquirySkeleton.setVisibility(GONE);
                if (response.isSuccessful() && response.body() != null) {
                    InquiryResponse res = response.body();
                    if(res.resultCode == 200 && res.data != null && !res.data.isEmpty()){
                        Log.d("inquiry","성공");
                        inquiryScroll.setVisibility(VISIBLE);
                        int incompleteColor = ContextCompat.getColor(requireContext(), R.color.incomplete);
                        int completeColor = ContextCompat.getColor(requireContext(), R.color.complete);
                        for (InquiryResponse.Inquiry inquiry : res.data) {
                            View card = inflater.inflate(R.layout.fragment_inquiry_list, inquiryList, false);

                            TextView title = card.findViewById(R.id.inquiry_title);
                            TextView complete = card.findViewById(R.id.inquiry_complete);

                            title.setText(inquiry.title);
                            if(inquiry.answered){
                                complete.setText("[답변 완료]");
                                complete.setTextColor(completeColor);
                            }else {
                                complete.setText("[처리중]");
                                complete.setTextColor(incompleteColor);
                            }

                            inquiryList.addView(card);
                            card.setOnClickListener(view -> {
                                // 문의 답변 띄우기
                                Bundle bundle = new Bundle();
                                bundle.putInt("inquiryId",inquiry.id);
                                bundle.putString("title",inquiry.title);
                                bundle.putString("content",inquiry.content);
                                if(inquiry.answered){
                                    bundle.putString("answer",inquiry.answer);
                                }
                                InquiryAnswerFragment inquiryAnswerFragment = new InquiryAnswerFragment();
                                inquiryAnswerFragment.setArguments(bundle);
                                ConstraintLayout inquiryLayout = requireView().findViewById(R.id.inquiry_layout);
                                inquiryLayout.setVisibility(GONE);
                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.inquiry_fragment_container, inquiryAnswerFragment)
                                        .addToBackStack(null)
                                        .commit();
                            });
                        }
                    }else {
                        inquiryScroll.setVisibility(GONE);
                        noListContainer.setVisibility(VISIBLE);
                        Log.d("inquiry", "문의 데이터 없음 또는 실패");
                    }
                }else {
                    inquiryScroll.setVisibility(GONE);
                    noListContainer.setVisibility(VISIBLE);
                    Log.d("inquiry", "연결 실패");
                }
            }

            @Override
            public void onFailure(Call<InquiryResponse> call, Throwable t) {
                inquirySkeleton.stopShimmer();
                inquirySkeleton.setVisibility(GONE);
                inquiryScroll.setVisibility(GONE);
                noListContainer.setVisibility(VISIBLE);
                Log.e("inquiry", "API call failed: " + t);
            }
        });
    }
}
