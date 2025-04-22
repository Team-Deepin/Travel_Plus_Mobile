package com.example.travelplus.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.travelplus.fragment.CourseFragment;
import com.example.travelplus.fragment.MainFragment;
import com.example.travelplus.fragment.MoreFragment;


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
            case 1:
                return new CourseFragment();
            case 2:
                return new MoreFragment();
            default:
                return new MainFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}