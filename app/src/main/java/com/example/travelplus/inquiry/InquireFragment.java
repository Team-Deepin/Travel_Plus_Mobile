package com.example.travelplus.inquiry;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class InquireFragment extends Fragment {
    EditText inquireTitle, inquireContent;
    ImageView inquireBtn;
    String title, content;
    ApiService apiService;
    private MockWebServer mockServer;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inquiry_send, container, false);
        setupMockServer();
        inquireTitle = view.findViewById(R.id.inquire_title);
        inquireContent = view.findViewById(R.id.inquire_content);
        inquireBtn = view.findViewById(R.id.inquire_btn);
        inquireBtn.setEnabled(false);

        inquireTitle.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                inquireContent.requestFocus();
                return true;
            }
            return false;
        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkInputAndSetButton();
            }
        };
        inquireTitle.addTextChangedListener(textWatcher);
        inquireContent.addTextChangedListener(textWatcher);

        inquireBtn.setOnClickListener(view1 -> {
            // 내용 보내기
            InquireRequest inquireRequest = new InquireRequest(title, content);
            Call<InquireResponse> call = apiService.inquire(inquireRequest);
            call.enqueue(new Callback<InquireResponse>() {
                @Override
                public void onResponse(Call<InquireResponse> call, Response<InquireResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        InquireResponse res = response.body();
                        Log.d("Inquire",res.resultMessage);
                        if (res.resultCode == 200) {
                            if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                            Toast.makeText(getContext(), "문의가 정상적으로 접수되었습니다.", Toast.LENGTH_SHORT).show();
                            Log.d("Inquire", "문의 성공");
                        }else {
                            Toast.makeText(getContext(), "문의 접수에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            Log.d("Inquire", "문의 실패");
                        }
                    }else {
                        Toast.makeText(getContext(), "문의 접수에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        Log.d("Inquire", "문의 실패");
                    }
                }

                @Override
                public void onFailure(Call<InquireResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "네트워크 연결 실패", Toast.LENGTH_SHORT).show();
                    Log.d("Inquire", "네트워크 연결 실패");
                }
            });
        });
        return view;
    }
    private void checkInputAndSetButton() {
        title = inquireTitle.getText().toString().trim();
        content = inquireContent.getText().toString().trim();

        if (!title.isEmpty() && !content.isEmpty()) {
            inquireBtn.setEnabled(true);
        } else {
            inquireBtn.setEnabled(false);
        }
    }
    private void setupMockServer() {
        new Thread(() -> {
            try {
                mockServer = new MockWebServer();
                mockServer.enqueue(new MockResponse()
                        .setResponseCode(200)
                        .setBody("{\"resultCode\":200,\"resultMessage\":\"Success\"}"));
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
