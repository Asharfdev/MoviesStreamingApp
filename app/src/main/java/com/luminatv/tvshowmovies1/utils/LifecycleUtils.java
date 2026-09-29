package com.luminatv.tvshowmovies1.utils;

import android.app.Activity;

import androidx.fragment.app.Fragment;

public final class LifecycleUtils {
    private LifecycleUtils() {
    }

    public static boolean isActivityAlive(Activity activity) {
        return activity != null && !activity.isFinishing() && !activity.isDestroyed();
    }

    public static boolean isFragmentViewAlive(Fragment fragment) {
        return fragment != null && fragment.isAdded() && fragment.getView() != null;
    }
}
