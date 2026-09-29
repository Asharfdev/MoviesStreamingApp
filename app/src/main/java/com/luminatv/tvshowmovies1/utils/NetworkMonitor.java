package com.luminatv.tvshowmovies1.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public final class NetworkMonitor {
    public interface Listener {
        void onNetworkAvailable();

        void onNetworkLost();
    }

    private final Context appContext;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ConnectivityManager.NetworkCallback networkCallback;
    private Listener listener;

    public NetworkMonitor(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public void start(@NonNull LifecycleOwner lifecycleOwner, @NonNull Listener listener) {
        this.listener = listener;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return;
        }

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                notifyAvailable();
            }

            @Override
            public void onLost(@NonNull Network network) {
                notifyLostIfOffline();
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities capabilities) {
                if (NetworkUtils.isNetworkAvailable(appContext)) {
                    notifyAvailable();
                } else {
                    notifyLostIfOffline();
                }
            }
        };

        try {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } catch (Exception exception) {
            networkCallback = null;
        }

        lifecycleOwner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                stop(connectivityManager);
            }
        });
    }

    private void notifyAvailable() {
        if (listener == null) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null && NetworkUtils.isNetworkAvailable(appContext)) {
                    listener.onNetworkAvailable();
                }
            }
        });
    }

    private void notifyLostIfOffline() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null && !NetworkUtils.isNetworkAvailable(appContext)) {
                    listener.onNetworkLost();
                }
            }
        });
    }

    private void stop(ConnectivityManager connectivityManager) {
        listener = null;
        if (connectivityManager != null && networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception exception) {
                // Ignore unregister failures during teardown.
            }
        }
        networkCallback = null;
    }
}
