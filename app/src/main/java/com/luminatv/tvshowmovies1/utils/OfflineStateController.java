package com.luminatv.tvshowmovies1.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.luminatv.tvshowmovies1.R;
import com.google.android.material.button.MaterialButton;

public final class OfflineStateController {
    public interface RetryCallback {
        void onRetry();
    }

    private final Context context;
    private final View offlineRoot;
    private final MaterialButton retryButton;
    private RetryCallback retryCallback;
    private NetworkMonitor networkMonitor;
    private boolean visible;
    private boolean reloadOnRestore;

    private OfflineStateController(@NonNull Context context, @NonNull View offlineRoot) {
        this.context = context.getApplicationContext();
        this.offlineRoot = offlineRoot;
        this.retryButton = offlineRoot.findViewById(R.id.btnOfflineRetry);
        this.retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRetryClick();
            }
        });
        offlineRoot.setAlpha(0f);
        offlineRoot.setVisibility(View.GONE);
    }

    public static OfflineStateController bind(@NonNull Context context, @NonNull View offlineRoot) {
        return new OfflineStateController(context, offlineRoot);
    }

    public void setRetryCallback(RetryCallback retryCallback) {
        this.retryCallback = retryCallback;
    }

    public void bindLifecycle(@NonNull LifecycleOwner lifecycleOwner) {
        networkMonitor = new NetworkMonitor(context);
        networkMonitor.start(lifecycleOwner, new NetworkMonitor.Listener() {
            @Override
            public void onNetworkAvailable() {
                if (visible || reloadOnRestore) {
                    hide(true);
                    if (retryCallback != null) {
                        retryCallback.onRetry();
                    }
                }
            }

            @Override
            public void onNetworkLost() {
                // Only show automatically when there is no cached content to display.
            }
        });
    }

    public boolean ensureOnlineOrShow() {
        if (NetworkUtils.isNetworkAvailable(context)) {
            return true;
        }
        show(false);
        return false;
    }

    public void showIfOfflineWithoutContent(boolean hasLoadedContent) {
        if (!hasLoadedContent && !NetworkUtils.isNetworkAvailable(context)) {
            show(false);
        }
    }

    public void handleLoadFailure(boolean hasLoadedContent) {
        if (!hasLoadedContent && !NetworkUtils.isNetworkAvailable(context)) {
            show(false);
        }
    }

    public void markContentLoaded() {
        reloadOnRestore = false;
        hide(true);
    }

    public boolean isVisible() {
        return visible;
    }

    public void show(boolean animate) {
        if (visible) {
            return;
        }
        visible = true;
        reloadOnRestore = true;
        offlineRoot.animate().cancel();
        offlineRoot.setVisibility(View.VISIBLE);
        if (!animate) {
            offlineRoot.setAlpha(1f);
            offlineRoot.setTranslationY(0f);
            return;
        }
        offlineRoot.setAlpha(0f);
        offlineRoot.setTranslationY(18f);
        offlineRoot.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(320L)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public void hide(boolean animate) {
        if (!visible) {
            return;
        }
        visible = false;
        offlineRoot.animate().cancel();
        if (!animate || offlineRoot.getVisibility() != View.VISIBLE) {
            offlineRoot.setAlpha(0f);
            offlineRoot.setVisibility(View.GONE);
            return;
        }
        offlineRoot.animate()
                .alpha(0f)
                .translationY(10f)
                .setDuration(220L)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        offlineRoot.setVisibility(View.GONE);
                        offlineRoot.setTranslationY(0f);
                    }
                })
                .start();
    }

    private void handleRetryClick() {
        if (NetworkUtils.isNetworkAvailable(context)) {
            hide(true);
            if (retryCallback != null) {
                retryCallback.onRetry();
            }
            return;
        }
        retryButton.setEnabled(false);
        offlineRoot.postDelayed(new Runnable() {
            @Override
            public void run() {
                retryButton.setEnabled(true);
                if (NetworkUtils.isNetworkAvailable(context)) {
                    hide(true);
                    if (retryCallback != null) {
                        retryCallback.onRetry();
                    }
                }
            }
        }, 450L);
    }
}
