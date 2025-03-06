package com.mtm.captaincalling;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HirePeopleActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    HirePeopleFragmentAdapter hireFragmentAdapter;
    private ImageView backButton;
    private FloatingActionButton imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire_people);
        Log.d(TAG, "onCreate: Layout set");

        // Set status and navigation bar colors for devices with Android M or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        // Initialize views
        tabLayout = findViewById(R.id.tab_layout_tournament);
        viewPager = findViewById(R.id.view_page_tournament);
        backButton = findViewById(R.id.back_tournament);
//        imageView = findViewById(R.id.admin_all_yt_videos);

//        // Check if the FloatingActionButton is null
//        if (imageView == null) {
//            Log.e(TAG, "onCreate: FloatingActionButton 'admin_youtube' not found in the layout.");
//            throw new NullPointerException("FloatingActionButton 'admin_youtube' not found in the layout.");
//        } else {
//            Log.d(TAG, "onCreate: FloatingActionButton 'admin_youtube' found");
//        }

        // Set click listener for the back button
        backButton.setOnClickListener(view -> {
            Log.d(TAG, "Back button clicked");
            onBackPressed();
        });

        // Set click listener for the FloatingActionButton
//        imageView.setOnClickListener(view -> {
//            Log.d(TAG, "FloatingActionButton clicked");
//            Intent intent = new Intent(getApplicationContext(), AllYoutubeVideoActivity.class);
//            intent.putExtra("Admin", "Yes");
//            startActivity(intent);
//        });

        // Set up the ViewPager and TabLayout
        hireFragmentAdapter = new HirePeopleFragmentAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(hireFragmentAdapter);

  //      tabLayout.addTab(tabLayout.newTab().setText("All Blogs"));
    //    tabLayout.addTab(tabLayout.newTab().setText("Review Pro Application"));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Hire Players");
                            break;
                        case 1:
                            tab.setText("Hire Staff");
                            break;
                    }
                }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}
