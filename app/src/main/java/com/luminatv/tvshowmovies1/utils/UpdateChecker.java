package com.luminatv.tvshowmovies1.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import androidx.browser.trusted.sharing.ShareTarget;
import com.luminatv.tvshowmovies1.R;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;

public class UpdateChecker {
    public static final String UPDATE_URL = "https://raw.githubusercontent.com/Asharfdev/huawei/refs/heads/main/moviebox%20zahira";

    public static void checkForUpdate(final Activity activity) {
        if (!LifecycleUtils.isActivityAlive(activity)) {
            return;
        }
        final WeakReference<Activity> activityRef = new WeakReference<>(activity);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlString = UPDATE_URL;
                    if (urlString.contains("?")) {
                        urlString += "&t=" + System.currentTimeMillis();
                    } else {
                        urlString += "?t=" + System.currentTimeMillis();
                    }
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(urlString).openConnection();
                    httpURLConnection.setRequestMethod(ShareTarget.METHOD_GET);
                    httpURLConnection.setConnectTimeout(10000);
                    httpURLConnection.setReadTimeout(10000);
                    if (httpURLConnection.getResponseCode() == 200) {
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(httpURLConnection.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }
                        bufferedReader.close();
                        JSONObject jSONObject = new JSONObject(sb.toString());
                        String strOptString = jSONObject.optString("newAppStatus", "off");
                        final String strOptString2 = jSONObject.optString("appPackage", "");
                        if ("on".equalsIgnoreCase(strOptString) && !strOptString2.isEmpty()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Activity currentActivity = activityRef.get();
                                    if (LifecycleUtils.isActivityAlive(currentActivity)) {
                                        showUpdateDialog(currentActivity, strOptString2);
                                    }
                                }
                            });
                        }
                    }
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                    // Update check is best-effort; ignore network/parse failures.
                } finally {
                    executorService.shutdown();
                }
            }
        });
    }

    private static void showUpdateDialog(final Activity activity, final String str) {
        if (!LifecycleUtils.isActivityAlive(activity)) {
            return;
        }
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_update);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
        }
        ((TextView) dialog.findViewById(R.id.btnUpdate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!LifecycleUtils.isActivityAlive(activity)) {
                    return;
                }
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + str)));
                } catch (Exception unused) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + str)));
                }
            }
        });
        dialog.show();
    }
}
