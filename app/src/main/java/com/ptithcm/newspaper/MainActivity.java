package com.ptithcm.newspaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.ptithcm.newspaper.adapter.ArticleAdapter;
import com.ptithcm.newspaper.api.ApiClient;
import com.ptithcm.newspaper.api.NewsApi;
import com.ptithcm.newspaper.model.Article;
import com.ptithcm.newspaper.model.RssResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TabLayout tabLayout;
    private ArticleAdapter adapter;
    private List<Article> articleList;
    private String currentRssUrl = "https://thanhnien.vn/rss/home.rss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo Toolbar
        Toolbar toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tabLayout = findViewById(R.id.tabLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        articleList = new ArrayList<>();
        adapter = new ArticleAdapter(this, articleList);
        recyclerView.setAdapter(adapter);

        setupTabs();

        swipeRefreshLayout.setOnRefreshListener(() -> fetchNews(currentRssUrl));

        fetchNews(currentRssUrl);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Trang chủ"));
        tabLayout.addTab(tabLayout.newTab().setText("Thời sự"));
        tabLayout.addTab(tabLayout.newTab().setText("Thế giới"));
        tabLayout.addTab(tabLayout.newTab().setText("Thể thao"));
        tabLayout.addTab(tabLayout.newTab().setText("Công nghệ"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currentRssUrl = "https://thanhnien.vn/rss/home.rss"; break;
                    case 1: currentRssUrl = "https://thanhnien.vn/rss/thoi-su.rss"; break;
                    case 2: currentRssUrl = "https://thanhnien.vn/rss/the-gioi.rss"; break;
                    case 3: currentRssUrl = "https://thanhnien.vn/rss/the-thao.rss"; break;
                    case 4: currentRssUrl = "https://thanhnien.vn/rss/cong-nghe.rss"; break; // Đã sửa link công nghệ
                }
                articleList.clear();
                adapter.notifyDataSetChanged();
                fetchNews(currentRssUrl);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void fetchNews(String rssUrl) {
        if (!swipeRefreshLayout.isRefreshing() && articleList.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
        }

        NewsApi newsApi = ApiClient.getClient().create(NewsApi.class);
        newsApi.getNewsList(rssUrl).enqueue(new Callback<RssResponse>() {
            @Override
            public void onResponse(Call<RssResponse> call, Response<RssResponse> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    articleList.clear();
                    articleList.addAll(response.body().getItems());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RssResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- TÍNH NĂNG TÌM KIẾM ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem searchItem = menu.add("Tìm kiếm").setIcon(android.R.drawable.ic_menu_search);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        SearchView searchView = new SearchView(this);
        searchItem.setActionView(searchView);
        searchView.setQueryHint("Nhập tên bài báo...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                List<Article> filteredList = new ArrayList<>();
                for (Article article : articleList) {
                    if (article.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.add(article);
                    }
                }
                adapter.filterList(filteredList);
                return true;
            }
        });
        return true;
    }
}