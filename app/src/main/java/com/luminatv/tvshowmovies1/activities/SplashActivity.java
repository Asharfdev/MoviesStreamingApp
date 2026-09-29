package com.luminatv.tvshowmovies1.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.luminatv.tvshowmovies1.onboarding.OnboardingActivity;
import com.luminatv.tvshowmovies1.onboarding.OnboardingPreferences;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Class<?> nextActivity = OnboardingPreferences.isCompleted(this)
                ? MainActivity.class
                : OnboardingActivity.class;
        startActivity(new Intent(this, nextActivity));
        finish();
        overridePendingTransition(0, 0);
    }
}
