package com.ptithcm.newspaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.newspaper.adapter.ArticleAdapter;
import com.ptithcm.newspaper.model.Article;

import java.util.List;

public class ReadingHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerHistory;
    private ArticleAdapter adapter;
    private TextView tvEmptyMessage;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_history);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarHistory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Lịch sử đọc");
        }

        recyclerHistory = findViewById(R.id.recyclerHistory);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        preferencesManager = new PreferencesManager(this);

        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        loadHistory();
    }

    private void loadHistory() {
        List<Article> history = preferencesManager.getHistory();

        if (history.isEmpty()) {
            tvEmptyMessage.setText("Chưa có lịch sử đọc. Bài viết bạn xem sẽ được lưu lại ở đây!");
            recyclerHistory.setVisibility(android.view.View.GONE);
            tvEmptyMessage.setVisibility(android.view.View.VISIBLE);
        } else {
            tvEmptyMessage.setVisibility(android.view.View.GONE);
            recyclerHistory.setVisibility(android.view.View.VISIBLE);
            adapter = new ArticleAdapter(this, history);
            recyclerHistory.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Xóa lịch sử")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == 1) {
            // Xóa toàn bộ lịch sử
            preferencesManager.clearHistory();
            Toast.makeText(this, "Đã xóa lịch sử đọc", Toast.LENGTH_SHORT).show();
            loadHistory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory(); // Cập nhật lại danh sách khi quay lại
    }
}

