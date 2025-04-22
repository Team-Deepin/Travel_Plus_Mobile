package com.example.travelplus.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travelplus.LoginActivity;
import com.example.travelplus.MainActivity;
import com.example.travelplus.R;
import com.example.travelplus.WithdrawTextView;

public class MoreFragment extends Fragment {
    ImageView notice, inquire, changeTheme, logout, withdraw;
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more,container,false);
        notice = view.findViewById(R.id.more_notice);
        inquire = view.findViewById(R.id.more_inquire);
        changeTheme = view.findViewById(R.id.more_change_theme);
        logout = view.findViewById(R.id.more_logout);
        withdraw = view.findViewById(R.id.more_withdraw_membership);
        logout.setOnClickListener(view1 -> {
            showLogoutPopup();
        });
        withdraw.setOnClickListener(view1 -> {
            showWithdrawPopup();
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
            // 로그아웃 API
            Toast.makeText(getActivity(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
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
            Toast.makeText(getActivity(), "회원탈퇴 되었습니다", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
        dialog.show();
    }
}
