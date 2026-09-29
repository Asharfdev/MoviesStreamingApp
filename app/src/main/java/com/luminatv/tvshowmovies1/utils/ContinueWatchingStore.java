package com.luminatv.tvshowmovies1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.luminatv.tvshowmovies1.models.WatchProgressItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class ContinueWatchingStore {
    private static final int COMPLETED_THRESHOLD = 95;
    private static final int MAX_ITEMS = 20;
    private static final String KEY_ITEMS = "continue_watching_progress";
    private static final String PREFS_NAME = "magis_tv_continue_watching";
    private static final Type LIST_TYPE = new TypeToken<ArrayList<WatchProgressItem>>() {
    }.getType();

    private static final Gson GSON = new Gson();

    private ContinueWatchingStore() {
    }

    public static List<WatchProgressItem> getContinueWatching(Context context) {
        List<WatchProgressItem> allItems = readItems(context);
        ArrayList<WatchProgressItem> visibleItems = new ArrayList<>();
        for (WatchProgressItem item : allItems) {
            if (item.getProgressPercentage() > 0 && item.getProgressPercentage() < COMPLETED_THRESHOLD) {
                visibleItems.add(item);
            }
        }
        return visibleItems;
    }

    public static WatchProgressItem getItem(Context context, int tmdbId, String type, int seasonNumber, int episodeNumber) {
        for (WatchProgressItem item : readItems(context)) {
            if (item.matches(tmdbId, type, seasonNumber, episodeNumber)) {
                return item;
            }
        }
        return null;
    }

    public static int getProgress(Context context, int tmdbId, String type, int seasonNumber, int episodeNumber) {
        WatchProgressItem item = getItem(context, tmdbId, type, seasonNumber, episodeNumber);
        return item == null ? 0 : item.getProgressPercentage();
    }

    public static void saveProgress(Context context, WatchProgressItem progressItem) {
        if (progressItem == null || progressItem.getTmdbId() <= 0 || progressItem.getType() == null) {
            return;
        }

        List<WatchProgressItem> items = readItems(context);
        removeMatching(items, progressItem);

        if (progressItem.getProgressPercentage() < COMPLETED_THRESHOLD) {
            progressItem.touch();
            items.add(0, progressItem);
        }

        while (items.size() > MAX_ITEMS) {
            items.remove(items.size() - 1);
        }
        writeItems(context, items);
    }

    private static void removeMatching(List<WatchProgressItem> items, WatchProgressItem progressItem) {
        for (int i = items.size() - 1; i >= 0; i--) {
            WatchProgressItem item = items.get(i);
            if (item.matches(progressItem.getTmdbId(), progressItem.getType(), progressItem.getSeasonNumber(), progressItem.getEpisodeNumber())) {
                items.remove(i);
            }
        }
    }

    private static List<WatchProgressItem> readItems(Context context) {
        String json = getPrefs(context).getString(KEY_ITEMS, "[]");
        try {
            ArrayList<WatchProgressItem> items = GSON.fromJson(json, LIST_TYPE);
            return items == null ? new ArrayList<WatchProgressItem>() : items;
        } catch (Exception exception) {
            getPrefs(context).edit().remove(KEY_ITEMS).apply();
            return new ArrayList<WatchProgressItem>();
        }
    }

    private static void writeItems(Context context, List<WatchProgressItem> items) {
        getPrefs(context).edit().putString(KEY_ITEMS, GSON.toJson(items)).apply();
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
