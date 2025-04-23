package com.example.travelplus;

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.travelplus.adapter.mainAdapter;
import com.example.travelplus.fragment.CourseDetailFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 mainView;
    private mainAdapter mainadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout mainTap = findViewById(R.id.mainTap);
        ViewPager2 mainView = findViewById(R.id.mainView);
        View detailBackground = findViewById(R.id.detail_background);

        mainAdapter adapter = new mainAdapter(this);
        mainView.setAdapter(adapter);
        new TabLayoutMediator(mainTap, mainView,
                (tab, position) -> {
                    tab.view.setOnClickListener(view -> {
                        detailBackground.setVisibility(GONE);
                    });
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