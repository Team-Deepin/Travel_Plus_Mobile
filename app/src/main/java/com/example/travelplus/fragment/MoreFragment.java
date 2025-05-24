package com.example.travelplus.fragment;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Context;
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

import com.example.travelplus.BaseResponse;
import com.example.travelplus.change.ChangeThemeFragment;
import com.example.travelplus.inquiry.InquiryFragment;
import com.example.travelplus.login.LoginActivity;
import com.example.travelplus.R;
import com.example.travelplus.WithdrawTextView;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;
import com.example.travelplus.notice.NoticeFragment;
import com.kakao.sdk.user.UserApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoreFragment extends Fragment {
    CardView notice, inquiry, changeTheme, logout, withdraw;
    ApiService apiService;
    ApiService logoutService;
    String loginType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = requireContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        loginType = prefs.getString("loginType", "normal");
    }

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
            if ("kakao".equals(loginType)){
                UserApiClient.getInstance().logout(error -> {
                    if (error != null) {
                        Log.e("KakaoLogout", "로그아웃 실패", error);
                    } else {
                        Log.i("KakaoLogout", "로그아웃 성공");
                        SharedPreferences prefs = requireContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
                        prefs.edit()
                                .remove("authorization")
                                .remove("loginType")
                                .apply();
                        dialog.dismiss();
                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                        Toast.makeText(getActivity(), "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    return null;
                });
            }else {
                Call<BaseResponse> call = logoutService.logout();
                call.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            BaseResponse res = response.body();
                            Log.d("Logout",res.resultMessage);
                            if (res.resultCode == 200) {
                                SharedPreferences preferences = requireContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.remove("loginType")
                                        .remove("authorization")
                                        .apply();
                                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                requireActivity().finish();
                                Toast.makeText(getActivity(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
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
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(getActivity(), "로그아웃 실패", Toast.LENGTH_SHORT).show();
                        Log.d("Logout","서버 연결 실패");
                        dialog.dismiss();
                    }
                });
            }
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
            checkBtn.setEnabled(false);
            // 회원탈퇴 API
            if ("kakao".equals(loginType)){
                UserApiClient.getInstance().unlink(error -> {
                    if (error != null) {
                        checkBtn.setEnabled(true);
                        Log.e("KakaoUnlink", "탈퇴 실패", error);
                        Toast.makeText(getActivity(), "카카오 탈퇴 실패", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                    Log.i("KakaoUnlink", "카카오 연결 해제 성공");
                    callWithdraw(dialog, checkBtn);
                    return null;
                });
            }else {
                callWithdraw(dialog, checkBtn);
            }
        });
        dialog.show();
    }
    private void callWithdraw(Dialog dialog, ImageView checkBtn) {
        Call<BaseResponse> call = apiService.withdraw();
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse res = response.body();
                    Log.d("Withdraw", res.resultMessage);
                    if (res.resultCode == 200) {
                        Toast.makeText(getActivity(), "회원탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        SharedPreferences prefs = requireContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
                        prefs.edit().clear().apply();
                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        checkBtn.setEnabled(true);
                        Toast.makeText(getActivity(), "회원탈퇴 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    checkBtn.setEnabled(true);
                    Toast.makeText(getActivity(), "회원탈퇴 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                checkBtn.setEnabled(true);
                Toast.makeText(getActivity(), "네트워크 연결 실패", Toast.LENGTH_SHORT).show();
                Log.e("Withdraw", "API call failed: ", t);
            }
        });
    }

}
