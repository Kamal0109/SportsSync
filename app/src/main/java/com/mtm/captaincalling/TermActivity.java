package com.mtm.captaincalling;


import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.InputStream;

import io.paperdb.Paper;

public class TermActivity extends AppCompatActivity {

    private TextView textViewTermm;
    private String key="";


    public void adjustFontScale(Configuration configuration) {
        if (configuration.fontScale > 0.92) {
            configuration.fontScale = (float) 0.92;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            getBaseContext().getResources().updateConfiguration(configuration, metrics);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.activity_term);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
        }

        textViewTermm = findViewById(R.id.hjghgfgxfdx);

        ImageView imageView = findViewById(R.id.jhvhgfxvh);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        getTermString();
    }

    private void getTermString() {
        try {
            Resources res = getResources(); InputStream in_s = res.openRawResource(R.raw.term);
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            textViewTermm.setText(new String(b));
        } catch (Exception e) {
            textViewTermm.setText("Error: can't show terms.");
        }
    }
}