package com.example.travelplus.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travelplus.LoginActivity;
import com.example.travelplus.MainActivity;
import com.example.travelplus.R;

public class MoreFragment extends Fragment {
    ImageView notice, inquire, changeTheme, logout;
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more,container,false);
        notice = view.findViewById(R.id.more_notice);
        inquire = view.findViewById(R.id.more_inquire);
        changeTheme = view.findViewById(R.id.more_change_theme);
        logout = view.findViewById(R.id.more_logout);

        logout.setOnClickListener(view1 -> {
            showLogoutPopup();
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
}
