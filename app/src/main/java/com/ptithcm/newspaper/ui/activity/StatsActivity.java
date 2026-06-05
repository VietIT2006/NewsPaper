package com.ptithcm.newspaper.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.util.OfflineManager;
import com.ptithcm.newspaper.util.PreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    private PreferencesManager preferencesManager;
    private OfflineManager offlineManager;

    private TextView tvStreak, tvTotalRead, tvTotalFavorites, tvTotalOffline;
    private LinearLayout layoutCategories;
    private TextView tvNoCategoryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Toolbar toolbar = findViewById(R.id.toolbarStats);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.stats_title));
        }

        preferencesManager = new PreferencesManager(this);
        offlineManager = new OfflineManager(this);
        
        tvStreak = findViewById(R.id.tvStreak);
        tvTotalRead = findViewById(R.id.tvTotalRead);
        tvTotalFavorites = findViewById(R.id.tvTotalFavorites);
        tvTotalOffline = findViewById(R.id.tvTotalOffline);
        layoutCategories = findViewById(R.id.layoutCategories);
        tvNoCategoryData = findViewById(R.id.tvNoCategoryData);

        loadStats();
    }

    private void loadStats() {
        int totalRead = preferencesManager.getHistory().size();
        int totalFavorites = preferencesManager.getFavorites().size();
        int totalOffline = offlineManager.getSavedArticleLinks().size();
        int streak = preferencesManager.getReadingStreak();

        tvTotalRead.setText(String.valueOf(totalRead));
        tvTotalFavorites.setText(String.valueOf(totalFavorites));
        tvTotalOffline.setText(getString(R.string.stats_total_offline, totalOffline));

        if (streak > 0) {
            tvStreak.setText(getString(R.string.stats_streak, streak));
        } else {
            tvStreak.setText(getString(R.string.stats_streak_zero));
        }

        loadCategoryStats();
    }

    private void loadCategoryStats() {
        Map<String, Integer> categoryCounts = preferencesManager.getCategoryCounts();
        
        if (categoryCounts.isEmpty()) {
            tvNoCategoryData.setVisibility(View.VISIBLE);
            return;
        }

        tvNoCategoryData.setVisibility(View.GONE);
        
        List<Map.Entry<String, Integer>> list = new ArrayList<>(categoryCounts.entrySet());
        list.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue())); // descending

        for (int i = 0; i < Math.min(5, list.size()); i++) {
            Map.Entry<String, Integer> entry = list.get(i);
            
            TextView tv = new TextView(this);
            tv.setText(entry.getKey() + " (" + entry.getValue() + " bài)");
            tv.setTextSize(16);
            tv.setTextColor(getResources().getColor(R.color.textPrimary, null));
            tv.setPadding(0, 8, 0, 8);
            
            layoutCategories.addView(tv);
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
