package com.ptithcm.newspaper.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
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
            getSupportActionBar().setTitle("Cài đặt");
        }

        preferencesManager = new PreferencesManager(this);
        
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);

        switchDarkMode.setChecked(preferencesManager.isDarkModeEnabled());
        switchNotifications.setChecked(preferencesManager.isNotificationsEnabled());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setDarkMode(isChecked);
            if (isChecked) {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(this, "Dark Mode bật", Toast.LENGTH_SHORT).show();
            } else {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(this, "Dark Mode tắt", Toast.LENGTH_SHORT).show();
            }
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setNotificationsEnabled(isChecked);
            Toast.makeText(this, isChecked ? "Thông báo bật" : "Thông báo tắt", Toast.LENGTH_SHORT).show();
        });
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

