package com.example.travelplus.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

        return view;
    }
}
