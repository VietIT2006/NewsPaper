package com.ptithcm.newspaper.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.os.LocaleListCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.util.PreferencesManager;

public class SettingsActivity extends AppCompatActivity {

    private PreferencesManager preferencesManager;
    private Switch switchDarkMode;
    private Switch switchNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.settings_title));
        }

        preferencesManager = new PreferencesManager(this);

        // Switches
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);

        switchDarkMode.setChecked(preferencesManager.isDarkModeEnabled());
        switchNotifications.setChecked(preferencesManager.isNotificationsEnabled());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setDarkMode(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(this, getString(R.string.dark_mode_on), Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(this, getString(R.string.dark_mode_off), Toast.LENGTH_SHORT).show();
            }
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setNotificationsEnabled(isChecked);
            Toast.makeText(this, isChecked ? getString(R.string.notifications_on) : getString(R.string.notifications_off), Toast.LENGTH_SHORT).show();
        });

        // Navigation buttons
        findViewById(R.id.btnFavorites).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));

        findViewById(R.id.btnHistory).setOnClickListener(v ->
                startActivity(new Intent(this, ReadingHistoryActivity.class)));

        findViewById(R.id.btnOffline).setOnClickListener(v ->
                startActivity(new Intent(this, SavedArticlesActivity.class)));

        findViewById(R.id.btnSources).setOnClickListener(v ->
                startActivity(new Intent(this, SourceManagerActivity.class)));

        findViewById(R.id.btnStats).setOnClickListener(v ->
                startActivity(new Intent(this, StatsActivity.class)));

        findViewById(R.id.btnAbout).setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class)));

        // Language switcher
        TextView tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage);
        updateLanguageLabel(tvCurrentLanguage);

        findViewById(R.id.btnLanguage).setOnClickListener(v -> {
            LocaleListCompat currentLocale = AppCompatDelegate.getApplicationLocales();
            if (currentLocale.isEmpty() || currentLocale.get(0).getLanguage().equals("vi")) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"));
            } else {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("vi"));
            }
        });
    }

    private void updateLanguageLabel(TextView tvCurrentLanguage) {
        LocaleListCompat currentLocale = AppCompatDelegate.getApplicationLocales();
        if (currentLocale.isEmpty() || currentLocale.get(0).getLanguage().equals("vi")) {
            tvCurrentLanguage.setText("Tiếng Việt");
        } else {
            tvCurrentLanguage.setText("English");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
