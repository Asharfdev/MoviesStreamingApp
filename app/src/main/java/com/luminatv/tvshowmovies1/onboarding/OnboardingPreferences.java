package com.luminatv.tvshowmovies1.onboarding;

import android.content.Context;
import android.content.SharedPreferences;

public final class OnboardingPreferences {
    private static final String PREFS_NAME = "magis_tv_onboarding";
    private static final String KEY_COMPLETED = "onboarding_complete";

    private OnboardingPreferences() {
    }

    public static boolean isCompleted(Context context) {
        return getPrefs(context).getBoolean(KEY_COMPLETED, false);
    }

    public static void setCompleted(Context context) {
        getPrefs(context).edit().putBoolean(KEY_COMPLETED, true).apply();
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
