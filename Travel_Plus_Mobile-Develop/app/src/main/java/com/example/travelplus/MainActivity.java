package com.example.travelplus;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.travelplus.adapter.mainAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout mainTap = findViewById(R.id.mainTap);
        ViewPager2 mainView = findViewById(R.id.mainView);
        mainAdapter adapter = new mainAdapter(this);
        mainView.setAdapter(adapter);
        mainView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });


        new TabLayoutMediator(mainTap, mainView,
                (tab, position) -> {

                    switch (position) {
                        case 0:
                            tab.setIcon(R.drawable.home);
                            break;
                        case 1:
                            tab.setIcon(R.drawable.location);
                            break;
                        case 2:
                            tab.setIcon(R.drawable.more);
                            break;
                    }
                }
        ).attach();
    }
}