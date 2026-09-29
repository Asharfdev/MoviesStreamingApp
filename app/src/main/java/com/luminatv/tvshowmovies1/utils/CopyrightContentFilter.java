package com.luminatv.tvshowmovies1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Loads and applies the remotely managed list of unavailable search titles. */
public final class CopyrightContentFilter {
    public static final String COPYRIGHT_NOTICE = "Sorry, this content is not available because it is copyrighted.";
    private static final String ASSET_FILE = "copyrighted_content.json";
    private static final String PREFS_NAME = "copyright_content_filter";
    private static final String PREFS_KEY = "copyrighted_keywords";

    public interface LoadCallback {
        void onLoaded();
    }

    private final Context context;
    private volatile List<String> keywords;

    public CopyrightContentFilter(Context context) {
        this.context = context.getApplicationContext();
        this.keywords = loadInitialKeywords();
    }

    public void refreshFromRemote(final LoadCallback callback) {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    String separator = UpdateChecker.UPDATE_URL.contains("?") ? "&" : "?";
                    URL url = new URL(UpdateChecker.UPDATE_URL + separator + "t=" + System.currentTimeMillis());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.setRequestMethod("GET");
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        JSONObject remoteConfig = new JSONObject(readStream(connection.getInputStream()));
                        JSONArray remoteArray = remoteConfig.optJSONArray("copyrightedKeywords");
                        if (remoteArray != null) {
                            List<String> remoteKeywords = parseKeywordsArray(remoteArray);
                            keywords = remoteKeywords;
                            saveKeywords(remoteKeywords);
                        }
                    }
                } catch (Exception ignored) {
                    // The cached or bundled list remains active when remote loading fails.
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    executor.shutdown();
                    if (callback != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onLoaded();
                            }
                        });
                    }
                }
            }
        });
    }

    public boolean isBlocked(String query) {
        String normalizedQuery = normalize(query);
        if (normalizedQuery.isEmpty()) {
            return false;
        }
        String compactQuery = normalizedQuery.replace(" ", "");
        for (String keyword : keywords) {
            String normalizedKeyword = normalize(keyword);
            String compactKeyword = normalizedKeyword.replace(" ", "");
            int halfLength = (compactKeyword.length() + 1) / 2;
            if (compactQuery.length() >= halfLength
                    && (compactKeyword.contains(compactQuery)
                    || compactQuery.contains(compactKeyword))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTitleBlocked(Context context, String title) {
        return new CopyrightContentFilter(context).isBlocked(title);
    }

    public static void showBlockedNotice(Context context) {
        Toast.makeText(context, COPYRIGHT_NOTICE, Toast.LENGTH_LONG).show();
    }

    private List<String> loadInitialKeywords() {
        String cached = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(PREFS_KEY, null);
        try {
            if (cached != null) {
                List<String> cachedKeywords = parseKeywordsArray(new JSONArray(cached));
                if (!cachedKeywords.isEmpty()) {
                    return cachedKeywords;
                }
            }
            return parseKeywords(readStream(context.getAssets().open(ASSET_FILE)));
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    private void saveKeywords(List<String> values) {
        JSONArray array = new JSONArray();
        for (String value : values) {
            array.put(value);
        }
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(PREFS_KEY, array.toString()).apply();
    }

    private static List<String> parseKeywords(String json) throws Exception {
        return parseKeywordsArray(new JSONObject(json).optJSONArray("copyrightedKeywords"));
    }

    private static List<String> parseKeywordsArray(JSONArray array) {
        if (array == null) {
            return Collections.emptyList();
        }
        List<String> values = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            String value = array.optString(i, "").trim();
            if (!value.isEmpty()) {
                values.add(value);
            }
        }
        return Collections.unmodifiableList(values);
    }

    private static String readStream(InputStream stream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        reader.close();
        return result.toString();
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
        return normalized.replaceAll("\\s+", " ");
    }
}
