package com.luminatv.tvshowmovies1;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.luminatv.tvshowmovies1.activities.ads.AppLAds;
import com.luminatv.tvshowmovies1.utils.CopyrightContentFilter;

public class MagisTvApplication extends Application {
    private static final String APP_ACTIVITY_PACKAGE = "com.luminatv.tvshowmovies1.";

    @Override
    public void onCreate() {
        super.onCreate();
        new CopyrightContentFilter(this).refreshFromRemote(null);
        registerActivityLifecycleCallbacks(new ForegroundCallbacks());
    }

    private boolean isOwnActivity(Activity activity) {
        return activity.getClass().getName().startsWith(APP_ACTIVITY_PACKAGE);
    }

    private final class ForegroundCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (isOwnActivity(activity)) {
                AppLAds.INSTANCE.setCurrentActivity(activity);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (isOwnActivity(activity)) {
                AppLAds.INSTANCE.clearCurrentActivity(activity);
            }
        }
    }
}
