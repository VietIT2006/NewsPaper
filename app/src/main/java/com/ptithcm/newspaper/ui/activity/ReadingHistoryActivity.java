package com.ptithcm.newspaper.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.data.model.Article;
import com.ptithcm.newspaper.ui.adapter.ArticleAdapter;
import com.ptithcm.newspaper.util.PreferencesManager;

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

        Toolbar toolbar = findViewById(R.id.toolbarHistory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.history_title));
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
            tvEmptyMessage.setText(getString(R.string.empty_history));
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
        menu.add(0, 1, 0, getString(R.string.delete_history))
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
            preferencesManager.clearHistory();
            Toast.makeText(this, getString(R.string.history_deleted), Toast.LENGTH_SHORT).show();
            loadHistory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }
}

