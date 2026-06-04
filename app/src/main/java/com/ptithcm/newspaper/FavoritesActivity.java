package com.ptithcm.newspaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.ptithcm.newspaper.adapter.ArticleAdapter;
import com.ptithcm.newspaper.model.Article;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerFavorites;
    private ArticleAdapter adapter;
    private TextView tvEmptyMessage;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarFavorites);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Bài viết yêu thích");
        }

        recyclerFavorites = findViewById(R.id.recyclerFavorites);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        preferencesManager = new PreferencesManager(this);

        recyclerFavorites.setLayoutManager(new LinearLayoutManager(this));
        loadFavorites();
    }

    private void loadFavorites() {
        List<Article> favorites = preferencesManager.getFavorites();

        if (favorites.isEmpty()) {
            tvEmptyMessage.setText("Chưa có bài viết yêu thích. Nhấn giữ bài viết để thêm vào danh sách yêu thích!");
            recyclerFavorites.setVisibility(android.view.View.GONE);
            tvEmptyMessage.setVisibility(android.view.View.VISIBLE);
        } else {
            tvEmptyMessage.setVisibility(android.view.View.GONE);
            recyclerFavorites.setVisibility(android.view.View.VISIBLE);
            adapter = new ArticleAdapter(this, favorites);
            recyclerFavorites.setAdapter(adapter);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites(); // Cập nhật lại danh sách khi quay lại
    }
}

