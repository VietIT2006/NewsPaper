package com.ptithcm.newspaper;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Application class để khởi tạo cấu hình toàn cầu
 */
public class NewsPaperApp extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Khởi tạo cấu hình Dark Mode từ SharedPreferences
        PreferencesManager prefsManager = new PreferencesManager(this);
        
        if (prefsManager.isDarkModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}

