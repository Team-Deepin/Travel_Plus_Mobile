package com.example.travelplus.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.travelplus.fragment.MainFragment;


public class mainAdapter extends FragmentStateAdapter {
    public mainAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MainFragment();
//            case 1:
//                return new fragment_trip();
//            case 2:
//                return new fragment_extra();
            default:
                return new MainFragment();
        }
    }

    // 탭의 개수
    @Override
    public int getItemCount() {
        return 3;
    }
}