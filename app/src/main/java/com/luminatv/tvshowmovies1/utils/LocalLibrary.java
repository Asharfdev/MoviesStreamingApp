package com.luminatv.tvshowmovies1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.luminatv.tvshowmovies1.models.LocalMediaItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class LocalLibrary {
    private static final int MAX_ITEMS = 24;
    private static final String KEY_CONTINUE_WATCHING = "continue_watching";
    private static final String KEY_FAVORITES = "favorites";
    private static final String KEY_RECENTLY_VIEWED = "recently_viewed";
    private static final String PREFS_NAME = "magis_tv_local_library";
    private static final Type LIST_TYPE = new TypeToken<ArrayList<LocalMediaItem>>() {
    }.getType();

    private static final Gson GSON = new Gson();

    private LocalLibrary() {
    }

    public static List<LocalMediaItem> getFavorites(Context context) {
        return readList(context, KEY_FAVORITES);
    }

    public static List<LocalMediaItem> getContinueWatching(Context context) {
        return readList(context, KEY_CONTINUE_WATCHING);
    }

    public static List<LocalMediaItem> getRecentlyViewed(Context context) {
        return readList(context, KEY_RECENTLY_VIEWED);
    }

    public static boolean isFavorite(Context context, int id, String type) {
        for (LocalMediaItem item : getFavorites(context)) {
            if (item.getId() == id && type != null && type.equals(item.getType())) {
                return true;
            }
        }
        return false;
    }

    public static boolean toggleFavorite(Context context, LocalMediaItem mediaItem) {
        if (mediaItem == null) {
            return false;
        }
        List<LocalMediaItem> items = getFavorites(context);
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSameMedia(mediaItem)) {
                items.remove(i);
                writeList(context, KEY_FAVORITES, items);
                return false;
            }
        }
        addToList(context, KEY_FAVORITES, mediaItem);
        return true;
    }

    public static void addRecentlyViewed(Context context, LocalMediaItem mediaItem) {
        addToList(context, KEY_RECENTLY_VIEWED, mediaItem);
    }

    public static void addContinueWatching(Context context, LocalMediaItem mediaItem) {
        addToList(context, KEY_CONTINUE_WATCHING, mediaItem);
    }

    private static void addToList(Context context, String key, LocalMediaItem mediaItem) {
        if (mediaItem == null) {
            return;
        }
        mediaItem.touch();
        List<LocalMediaItem> items = readList(context, key);
        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i).isSameMedia(mediaItem)) {
                items.remove(i);
            }
        }
        items.add(0, mediaItem);
        while (items.size() > MAX_ITEMS) {
            items.remove(items.size() - 1);
        }
        writeList(context, key, items);
    }

    private static List<LocalMediaItem> readList(Context context, String key) {
        String json = getPrefs(context).getString(key, "[]");
        try {
            ArrayList<LocalMediaItem> items = GSON.fromJson(json, LIST_TYPE);
            return items == null ? new ArrayList<LocalMediaItem>() : items;
        } catch (Exception exception) {
            getPrefs(context).edit().remove(key).apply();
            return new ArrayList<LocalMediaItem>();
        }
    }

    private static void writeList(Context context, String key, List<LocalMediaItem> items) {
        getPrefs(context).edit().putString(key, GSON.toJson(items)).apply();
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
