package com.example.travelplus.fragment;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.travelplus.change.ChangeThemeFragment;
import com.example.travelplus.inquiry.InquiryFragment;
import com.example.travelplus.login.LoginActivity;
import com.example.travelplus.login.LogoutResponse;
import com.example.travelplus.R;
import com.example.travelplus.WithdrawTextView;
import com.example.travelplus.login.WithdrawResponse;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;
import com.example.travelplus.notice.NoticeFragment;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoreFragment extends Fragment {
    CardView notice, inquiry, changeTheme, logout, withdraw;
    ApiService apiService;
    ApiService logoutService;
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more,container,false);
        apiService = RetrofitClient.getApiInstance(requireContext()).create(ApiService.class);
        logoutService = RetrofitClient.getLoginInstance().create(ApiService.class);
        notice = view.findViewById(R.id.more_notice);
        inquiry = view.findViewById(R.id.more_inquiry);
        changeTheme = view.findViewById(R.id.more_change_theme);
        logout = view.findViewById(R.id.more_logout);
        withdraw = view.findViewById(R.id.more_withdraw_membership);
        notice.setOnClickListener(view1 -> {
            NoticeFragment noticeFragment = new NoticeFragment();
            ConstraintLayout moreLayout = view.findViewById(R.id.more_layout);
            moreLayout.setVisibility(GONE);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.more_fragment_container, noticeFragment)
                    .addToBackStack(null)
                    .commit();
        });
        inquiry.setOnClickListener(view1 -> {
            InquiryFragment inquiryFragment = new InquiryFragment();
            ConstraintLayout moreLayout = view.findViewById(R.id.more_layout);
            moreLayout.setVisibility(GONE);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.more_fragment_container, inquiryFragment)
                    .addToBackStack(null)
                    .commit();
        });
        changeTheme.setOnClickListener(view1 -> {
            ChangeThemeFragment changeThemeFragment = new ChangeThemeFragment();
            ConstraintLayout moreLayout = view.findViewById(R.id.more_layout);
            moreLayout.setVisibility(GONE);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.more_fragment_container, changeThemeFragment)
                    .addToBackStack(null)
                    .commit();
        });
        logout.setOnClickListener(view1 -> {
            showLogoutPopup();
        });
        withdraw.setOnClickListener(view1 -> {
            showWithdrawPopup();
        });
        requireActivity().getSupportFragmentManager()
                .addOnBackStackChangedListener(()->{
                    if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() == 0){
                        ConstraintLayout moreLayout = view.findViewById(R.id.more_layout);
                        moreLayout.setVisibility(VISIBLE);
                    }
                });
        return view;
    }
    private void showLogoutPopup(){
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.pop_up_logout);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_input, null));
        }
        ImageView cancelBtn = dialog.findViewById(R.id.cancel_button);
        ImageView checkBtn = dialog.findViewById(R.id.check_button);
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        checkBtn.setOnClickListener(v -> {
            Call<LogoutResponse> call = logoutService.logout();
            call.enqueue(new Callback<LogoutResponse>() {
                @Override
                public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        LogoutResponse res = response.body();
                        Log.d("Logout",res.resultMessage);
                        if (res.resultCode == 200) {
                            Toast.makeText(getActivity(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            SharedPreferences preferences = requireContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.remove("authorization");
                            editor.apply();

                            Intent intent = new Intent(requireActivity(), LoginActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        }else{
                            Toast.makeText(getActivity(), "로그아웃 실패", Toast.LENGTH_SHORT).show();
                            Log.d("Logout",String.valueOf(res.resultCode));
                            dialog.dismiss();
                        }
                    }else {
                        Toast.makeText(getActivity(), "로그아웃 실패", Toast.LENGTH_SHORT).show();
                        Log.d("Logout","로그아웃 실패");
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<LogoutResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), "로그아웃 실패", Toast.LENGTH_SHORT).show();
                    Log.d("Logout","서버 연결 실패");
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }
    private void showWithdrawPopup(){
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.pop_up_withdraw);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_input, null));
        }
        WithdrawTextView checkText = dialog.findViewById(R.id.check_text);
        CheckBox withdrawCheck = dialog.findViewById(R.id.withdraw_check);
        checkText.setPaintFlags(checkText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        ImageView cancelBtn = dialog.findViewById(R.id.cancel_button);
        ImageView checkBtn = dialog.findViewById(R.id.check_button);
        checkBtn.setEnabled(false);
        withdrawCheck.setOnCheckedChangeListener((buttonView,isChecked)-> {
            checkBtn.setEnabled(isChecked);
        });
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        checkBtn.setOnClickListener(v -> {
            // 회원탈퇴 API
            WithdrawResponse withdrawResponse = new WithdrawResponse();
            Call<WithdrawResponse> call = apiService.withdraw();
            call.enqueue(new Callback<WithdrawResponse>() {
                @Override
                public void onResponse(Call<WithdrawResponse> call, Response<WithdrawResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        WithdrawResponse res = response.body();
                        Log.d("Withdraw", res.resultMessage);
                        if (res.resultCode == 200) {
                            Log.d("Withdraw", "회원탈퇴 성공");
                            Toast.makeText(getActivity(), "회원탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            Intent intent = new Intent(requireActivity(), LoginActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        }else {
                            Log.d("Withdraw", "회원탈퇴 실패");
                            Toast.makeText(getActivity(), "회원탈퇴를 완료할 수 없습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }else {
                        Log.d("Withdraw", "회원탈퇴 실패");
                        Toast.makeText(getActivity(), "회원탈퇴를 완료할 수 없습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
                @Override
                public void onFailure(Call<WithdrawResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), "네트워크 연결 실패", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    Log.e("Withdraw","API call failed: " + t);
                }
            });

        });
        dialog.show();
    }
}
