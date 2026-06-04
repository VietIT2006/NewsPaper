package com.ptithcm.newspaper;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ptithcm.newspaper.model.Article;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý lưu trữ dữ liệu bài viết (Favorites, History, Settings...)
 */
public class PreferencesManager {
    private static final String PREF_FAVORITES = "FAVORITES";
    private static final String PREF_HISTORY = "READING_HISTORY";
    private static final String PREF_SETTINGS = "SETTINGS";
    private static final String KEY_ARTICLES = "articles";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    
    private SharedPreferences favPrefs;
    private SharedPreferences historyPrefs;
    private SharedPreferences settingsPrefs;
    private Gson gson;

    public PreferencesManager(Context context) {
        this.favPrefs = context.getSharedPreferences(PREF_FAVORITES, Context.MODE_PRIVATE);
        this.historyPrefs = context.getSharedPreferences(PREF_HISTORY, Context.MODE_PRIVATE);
        this.settingsPrefs = context.getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    // --- FAVORITES METHODS ---
    public void addFavorite(Article article) {
        List<Article> favorites = getFavorites();
        // Kiểm tra không trùng
        for (Article fav : favorites) {
            if (fav.getLink().equals(article.getLink())) {
                return; // Đã tồn tại
            }
        }
        favorites.add(article);
        saveFavorites(favorites);
    }

    public void removeFavorite(String articleLink) {
        List<Article> favorites = getFavorites();
        favorites.removeIf(article -> article.getLink().equals(articleLink));
        saveFavorites(favorites);
    }

    public List<Article> getFavorites() {
        String json = favPrefs.getString(KEY_ARTICLES, "[]");
        Type type = new TypeToken<ArrayList<Article>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public boolean isFavorite(String articleLink) {
        for (Article article : getFavorites()) {
            if (article.getLink().equals(articleLink)) {
                return true;
            }
        }
        return false;
    }

    private void saveFavorites(List<Article> articles) {
        favPrefs.edit().putString(KEY_ARTICLES, gson.toJson(articles)).apply();
    }

    // --- READING HISTORY METHODS ---
    public void addToHistory(Article article) {
        List<Article> history = getHistory();
        // Xóa nếu đã tồn tại (để đặt lại vị trí)
        history.removeIf(a -> a.getLink().equals(article.getLink()));
        // Thêm vào đầu danh sách
        history.add(0, article);
        // Giữ tối đa 100 bài
        if (history.size() > 100) {
            history = new ArrayList<>(history.subList(0, 100));
        }
        saveHistory(history);
    }

    public List<Article> getHistory() {
        String json = historyPrefs.getString(KEY_ARTICLES, "[]");
        Type type = new TypeToken<ArrayList<Article>>(){}.getType();
        return gson.fromJson(json, type);
    }

    private void saveHistory(List<Article> articles) {
        historyPrefs.edit().putString(KEY_ARTICLES, gson.toJson(articles)).apply();
    }

    public void clearHistory() {
        historyPrefs.edit().remove(KEY_ARTICLES).apply();
    }

    // --- SETTINGS METHODS ---
    public void setDarkMode(boolean enabled) {
        settingsPrefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    public boolean isDarkModeEnabled() {
        return settingsPrefs.getBoolean(KEY_DARK_MODE, false);
    }

    public void setNotificationsEnabled(boolean enabled) {
        settingsPrefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }

    public boolean isNotificationsEnabled() {
        return settingsPrefs.getBoolean(KEY_NOTIFICATIONS, true);
    }
}

