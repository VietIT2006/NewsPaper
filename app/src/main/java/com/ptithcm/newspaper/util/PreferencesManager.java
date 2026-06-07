package com.ptithcm.newspaper.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ptithcm.newspaper.data.model.Article;
import com.ptithcm.newspaper.data.model.RssSource;
import com.ptithcm.newspaper.data.model.User;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Quản lý lưu trữ dữ liệu bài viết (Favorites, History, Settings, Sources, Stats...)
 */
public class PreferencesManager {
    private static final String PREF_FAVORITES = "FAVORITES";
    private static final String PREF_HISTORY = "READING_HISTORY";
    private static final String PREF_SETTINGS = "SETTINGS";
    private static final String PREF_RSS_SOURCES = "RSS_SOURCES";
    private static final String PREF_STATS = "READING_STATS";
    private static final String KEY_ARTICLES = "articles";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    private static final String KEY_SOURCES = "sources";
    private static final String KEY_DAILY_COUNTS = "daily_counts";
    private static final String KEY_CATEGORY_COUNTS = "category_counts";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final String KEY_FONT_FAMILY = "font_family";
    private static final String KEY_READER_BG = "reader_bg";
    private static final String KEY_CURRENT_USER = "current_user";
    
    private SharedPreferences favPrefs;
    private SharedPreferences historyPrefs;
    private SharedPreferences settingsPrefs;
    private SharedPreferences rssPrefs;
    private SharedPreferences statsPrefs;
    private Gson gson;

    public PreferencesManager(Context context) {
        this.favPrefs = context.getSharedPreferences(PREF_FAVORITES, Context.MODE_PRIVATE);
        this.historyPrefs = context.getSharedPreferences(PREF_HISTORY, Context.MODE_PRIVATE);
        this.settingsPrefs = context.getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE);
        this.rssPrefs = context.getSharedPreferences(PREF_RSS_SOURCES, Context.MODE_PRIVATE);
        this.statsPrefs = context.getSharedPreferences(PREF_STATS, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    // --- FAVORITES METHODS ---
    public void addFavorite(Article article) {
        List<Article> favorites = getFavorites();
        for (Article fav : favorites) {
            if (fav.getLink().equals(article.getLink())) {
                return;
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

    public void updateFavorite(Article updatedArticle) {
        List<Article> favorites = getFavorites();
        for (int i = 0; i < favorites.size(); i++) {
            if (favorites.get(i).getLink().equals(updatedArticle.getLink())) {
                favorites.set(i, updatedArticle);
                break;
            }
        }
        saveFavorites(favorites);
    }

    private void saveFavorites(List<Article> articles) {
        favPrefs.edit().putString(KEY_ARTICLES, gson.toJson(articles)).apply();
    }

    // --- READING HISTORY METHODS ---
    public void addToHistory(Article article) {
        List<Article> history = getHistory();
        history.removeIf(a -> a.getLink().equals(article.getLink()));
        history.add(0, article);
        if (history.size() > 100) {
            history = new ArrayList<>(history.subList(0, 100));
        }
        saveHistory(history);
        
        // Update daily reading stats
        incrementDailyCount();
        
        // Update category stats
        if (article.getCategory() != null && !article.getCategory().isEmpty()) {
            incrementCategoryCount(article.getCategory());
        }
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

    // --- READER MODE SETTINGS ---
    public void setFontSize(int size) {
        settingsPrefs.edit().putInt(KEY_FONT_SIZE, size).apply();
    }

    public int getFontSize() {
        return settingsPrefs.getInt(KEY_FONT_SIZE, 18);
    }

    public void setFontFamily(String fontFamily) {
        settingsPrefs.edit().putString(KEY_FONT_FAMILY, fontFamily).apply();
    }

    public String getFontFamily() {
        return settingsPrefs.getString(KEY_FONT_FAMILY, "sans-serif");
    }

    public void setReaderBackground(String bg) {
        settingsPrefs.edit().putString(KEY_READER_BG, bg).apply();
    }

    public String getReaderBackground() {
        return settingsPrefs.getString(KEY_READER_BG, "white");
    }

    // --- RSS SOURCES METHODS ---
    public List<RssSource> getRssSources() {
        String json = rssPrefs.getString(KEY_SOURCES, null);
        if (json == null) {
            // Return defaults
            List<RssSource> defaults = new ArrayList<>();
            defaults.add(new RssSource(1, "Thanh Niên", "https://thanhnien.vn/rss/home.rss", true));
            defaults.add(new RssSource(2, "VnExpress", "https://vnexpress.net/rss/tin-moi-nhat.rss", false));
            defaults.add(new RssSource(3, "Tuổi Trẻ", "https://tuoitre.vn/rss/tin-moi-nhat.rss", false));
            defaults.add(new RssSource(4, "Dân Trí", "https://dantri.com.vn/rss/home.rss", false));
            defaults.add(new RssSource(5, "Zing News", "https://zingnews.vn/rss/tin-moi.rss", false));
            saveRssSources(defaults);
            return defaults;
        }
        Type type = new TypeToken<ArrayList<RssSource>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void saveRssSources(List<RssSource> sources) {
        rssPrefs.edit().putString(KEY_SOURCES, gson.toJson(sources)).apply();
    }

    public List<RssSource> getEnabledSources() {
        List<RssSource> all = getRssSources();
        List<RssSource> enabled = new ArrayList<>();
        for (RssSource source : all) {
            if (source.isEnabled()) {
                enabled.add(source);
            }
        }
        return enabled;
    }

    // --- READING STATS METHODS ---
    private String getTodayKey() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public void incrementDailyCount() {
        Map<String, Integer> counts = getDailyCounts();
        String today = getTodayKey();
        counts.put(today, counts.getOrDefault(today, 0) + 1);
        
        // Keep only last 30 days
        if (counts.size() > 30) {
            List<String> keys = new ArrayList<>(counts.keySet());
            java.util.Collections.sort(keys);
            while (keys.size() > 30) {
                counts.remove(keys.remove(0));
            }
        }
        
        statsPrefs.edit().putString(KEY_DAILY_COUNTS, gson.toJson(counts)).apply();
    }

    public Map<String, Integer> getDailyCounts() {
        String json = statsPrefs.getString(KEY_DAILY_COUNTS, "{}");
        Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
        Map<String, Integer> result = gson.fromJson(json, type);
        return result != null ? result : new HashMap<>();
    }

    public void incrementCategoryCount(String category) {
        Map<String, Integer> counts = getCategoryCounts();
        counts.put(category, counts.getOrDefault(category, 0) + 1);
        statsPrefs.edit().putString(KEY_CATEGORY_COUNTS, gson.toJson(counts)).apply();
    }

    public Map<String, Integer> getCategoryCounts() {
        String json = statsPrefs.getString(KEY_CATEGORY_COUNTS, "{}");
        Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
        Map<String, Integer> result = gson.fromJson(json, type);
        return result != null ? result : new HashMap<>();
    }

    public int getReadingStreak() {
        Map<String, Integer> counts = getDailyCounts();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        int streak = 0;
        java.util.Calendar cal = java.util.Calendar.getInstance();
        
        while (true) {
            String key = sdf.format(cal.getTime());
            if (counts.containsKey(key) && counts.get(key) > 0) {
                streak++;
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1);
            } else {
                break;
            }
        }
        return streak;
    }

    // --- AUTH METHODS ---
    public void saveCurrentUser(User user) {
        settingsPrefs.edit().putString(KEY_CURRENT_USER, gson.toJson(user)).apply();
    }

    public User getCurrentUser() {
        String json = settingsPrefs.getString(KEY_CURRENT_USER, null);
        if (json == null) return null;
        return gson.fromJson(json, User.class);
    }

    public void logout() {
        settingsPrefs.edit().remove(KEY_CURRENT_USER).apply();
    }
}
