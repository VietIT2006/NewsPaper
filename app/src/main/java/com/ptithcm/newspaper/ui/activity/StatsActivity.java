package com.ptithcm.newspaper.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.util.PreferencesManager;

import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private PreferencesManager preferencesManager;
    private TextView tvTotalRead, tvTotalFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

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
        int totalRead = preferencesManager.getHistory().size();
        int totalFavorites = preferencesManager.getFavorites().size();

        tvTotalRead.setText("Bài viết đã đọc: " + totalRead);
        tvTotalFavorites.setText("Bài viết yêu thích: " + totalFavorites);

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

