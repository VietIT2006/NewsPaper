package com.ptithcm.newspaper.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.data.model.Article;
import com.ptithcm.newspaper.ui.adapter.ArticleAdapter;
import com.ptithcm.newspaper.util.OfflineManager;
import com.ptithcm.newspaper.util.PreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedArticlesActivity extends AppCompatActivity {

    private RecyclerView recyclerSaved;
    private ArticleAdapter adapter;
    private TextView tvEmptyMessage;
    private OfflineManager offlineManager;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_articles);

        Toolbar toolbar = findViewById(R.id.toolbarSaved);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("📥 Bài viết đã lưu");
        }

        recyclerSaved = findViewById(R.id.recyclerSaved);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        offlineManager = new OfflineManager(this);
        preferencesManager = new PreferencesManager(this);

        recyclerSaved.setLayoutManager(new LinearLayoutManager(this));
        loadSavedArticles();
    }

    private void loadSavedArticles() {
        List<String> savedLinks = offlineManager.getSavedArticleLinks();

        if (savedLinks.isEmpty()) {
            tvEmptyMessage.setText("Chưa có bài viết nào được lưu offline.");
            recyclerSaved.setVisibility(View.GONE);
            tvEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            recyclerSaved.setVisibility(View.VISIBLE);

            // Tạo map link -> Article từ history và favorites
            Map<String, Article> articleMap = new HashMap<>();
            for (Article article : preferencesManager.getHistory()) {
                articleMap.put(article.getLink(), article);
            }
            for (Article article : preferencesManager.getFavorites()) {
                if (!articleMap.containsKey(article.getLink())) {
                    articleMap.put(article.getLink(), article);
                }
            }

            // Tạo danh sách Article từ saved links
            List<Article> articles = new ArrayList<>();
            for (String link : savedLinks) {
                if (articleMap.containsKey(link)) {
                    articles.add(articleMap.get(link));
                } else {
                    // Nếu không tìm thấy trong history/favorites, tạo Article cơ bản
                    Article article = new Article();
                    article.setLink(link);
                    article.setTitle("Bài viết đã lưu offline");
                    articles.add(article);
                }
            }

            adapter = new ArticleAdapter(this, articles);
            recyclerSaved.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Xoá tất cả")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == 1) {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có muốn xoá tất cả bài viết đã lưu offline?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        offlineManager.clearAll();
                        Toast.makeText(this, "Đã xoá tất cả bài viết offline.", Toast.LENGTH_SHORT).show();
                        loadSavedArticles();
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSavedArticles();
    }
}
