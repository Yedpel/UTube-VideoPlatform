package com.example.utube.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.utube.R;

public class FakeIntermediateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_intermediate);

        // Get the current theme mode from the intent
        boolean isNightMode = getIntent().getBooleanExtra("IS_NIGHT_MODE", false);
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        // Apply the theme change
//        AppCompatDelegate.setDefaultNightMode(isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
//
//        getDelegate().applyDayNight();
        // Delay to allow theme change to apply before returning to MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(FakeIntermediateActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 1000); // 500ms delay to show loading animation
    }
}
