package com.ptithcm.newspaper;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
// THÊM DÒNG IMPORT NÀY NẾU CHƯA CÓ
import com.ptithcm.newspaper.util.PreferencesManager;

public class NewsPaperApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesManager prefsManager = new PreferencesManager(this);
        if (prefsManager.isDarkModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}