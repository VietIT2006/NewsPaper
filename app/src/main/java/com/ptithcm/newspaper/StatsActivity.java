package com.ptithcm.newspaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private PreferencesManager preferencesManager;
    private TextView tvTotalRead, tvTotalFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarStats);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thống kê");
        }

        preferencesManager = new PreferencesManager(this);
        
        tvTotalRead = findViewById(R.id.tvTotalRead);
        tvTotalFavorites = findViewById(R.id.tvTotalFavorites);

        loadStats();
    }

    private void loadStats() {
        List history = preferencesManager.getHistory();
        List favorites = preferencesManager.getFavorites();

        int totalRead = history.size();
        int totalFavorites = favorites.size();

        tvTotalRead.setText("Bài viết đã đọc: " + totalRead);
        tvTotalFavorites.setText("Bài viết yêu thích: " + totalFavorites);

        // Thêm một số thông tin khác
        if (totalRead > 0) {
            TextView tvExtraInfo = findViewById(R.id.tvExtraInfo);
            tvExtraInfo.setText("Bạn đã dành thời gian để đọc " + totalRead + " bài viết. Tiếp tục theo dõi những tin tức mới nhất!");
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

