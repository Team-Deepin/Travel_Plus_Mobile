package com.example.travelplus.inquiry;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
    private MockWebServer mockServer;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inquiry, container, false);
        setupMockServer(inflater);
        noListContainer = view.findViewById(R.id.inquiry_no_list_container);
        ImageView inquiryBtn = view.findViewById(R.id.inquiry_btn);
        inquiryScroll = view.findViewById(R.id.inquiry_scroll);
        inquiryList = view.findViewById(R.id.inquiry_list);
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
        Call<InquiryResponse> call = apiService.inquiry();
        call.enqueue(new Callback<InquiryResponse>() {
            @Override
            public void onResponse(Call<InquiryResponse> call, Response<InquiryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InquiryResponse res = response.body();
                    Log.d("inquiry",res.result_message);
                    if(res.result_code == 200 && res.data != null && !res.data.isEmpty()){
                        Log.d("inquiry","성공");
                        inquiryScroll.setVisibility(VISIBLE);
                        noListContainer.setVisibility(GONE);
                        int incompleteColor = ContextCompat.getColor(requireContext(), R.color.incomplete);
                        int completeColor = ContextCompat.getColor(requireContext(), R.color.complete);
                        for (InquiryResponse.Inquiry inquiry : res.data) {
                            View card = inflater.inflate(R.layout.fragment_inquiry_list, inquiryList, false);

                            TextView title = card.findViewById(R.id.inquiry_title);
                            TextView complete = card.findViewById(R.id.inquiry_complete);

                            title.setText(inquiry.title);
                            if(inquiry.isAnswered){
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
                                bundle.putInt("inquiryId",inquiry.inquireId);
                                bundle.putString("title",inquiry.title);
                                bundle.putString("content",inquiry.content);
                                if(inquiry.isAnswered){
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
                inquiryScroll.setVisibility(GONE);
                noListContainer.setVisibility(VISIBLE);
                Log.e("inquiry", "API call failed: " + t);
            }
        });
    }
    private void setupMockServer(LayoutInflater inflater) {
        new Thread(() -> {
            try {
                mockServer = new MockWebServer();
                mockServer.enqueue(new MockResponse()
                        .setResponseCode(200)
                        .setBody("{\n" +
                                "  \"result_code\": 200,\n" +
                                "  \"result_message\": \"Success\",\n" +
                                "  \"data\": [\n" +
                                "    {\n" +
                                "      \"inquireId\": 1,\n" +
                                "      \"title\": \"여행 일정 변경 문의 합니다. 제목을 늘려봐요\",\n" +
                                "      \"content\": \"예약한 여행 일정 변경이 가능한가요?\",\n" +
                                "      \"createDate\": \"2024-03-30T10:15:00\",\n" +
                                "      \"isAnswered\": true,\n" +
                                "      \"answer\": \"네, 변경 가능합니다. 자세한 내용은 고객센터로 문의해주세요.\",\n" +
                                "      \"answerDate\": \"2024-03-31T09:00:00\"\n" +
                                "    },\n" +
                                "    {\n" +
                                "      \"inquireId\": 2,\n" +
                                "      \"title\": \"탈퇴 관련 문의\",\n" +
                                "      \"content\": \"회원 탈퇴 요청드립니다\",\n" +
                                "      \"createDate\": \"2024-03-28T14:20:00\",\n" +
                                "      \"isAnswered\": false,\n" +
                                "      \"answer\": null,\n" +
                                "      \"answerDate\": null\n" +
                                "    }\n" +
                                "  ]\n" +
                                "}"));
                mockServer.start();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(mockServer.url("/"))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                apiService = retrofit.create(ApiService.class);

                getActivity().runOnUiThread(() -> inquiryLists(inflater));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
