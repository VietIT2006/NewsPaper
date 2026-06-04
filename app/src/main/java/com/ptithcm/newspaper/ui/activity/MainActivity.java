package com.ptithcm.newspaper.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.data.model.Article;
import com.ptithcm.newspaper.data.model.RssResponse;
import com.ptithcm.newspaper.data.remote.ApiClient;
import com.ptithcm.newspaper.data.remote.NewsApi;
import com.ptithcm.newspaper.ui.adapter.ArticleAdapter;
import com.ptithcm.newspaper.util.PreferencesManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerToday, recyclerSuggest;
    private TextView tvTitleToday, tvTitleSuggest;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TabLayout tabLayout;

    private ArticleAdapter todayAdapter, suggestAdapter;
    private List<Article> articleList;
    private List<Article> todayList, suggestList;
    private String currentRssUrl = "https://thanhnien.vn/rss/home.rss";
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        preferencesManager = new PreferencesManager(this);
        
        setContentView(R.layout.activity_main);

        Toolbar toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);

        recyclerToday = findViewById(R.id.recyclerToday);
        recyclerSuggest = findViewById(R.id.recyclerSuggest);
        tvTitleToday = findViewById(R.id.tvTitleToday);
        tvTitleSuggest = findViewById(R.id.tvTitleSuggest);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tabLayout = findViewById(R.id.tabLayout);

        recyclerToday.setLayoutManager(new LinearLayoutManager(this));
        recyclerSuggest.setLayoutManager(new LinearLayoutManager(this));

        articleList = new ArrayList<>();
        todayList = new ArrayList<>();
        suggestList = new ArrayList<>();

        todayAdapter = new ArticleAdapter(this, todayList);
        suggestAdapter = new ArticleAdapter(this, suggestList);

        recyclerToday.setAdapter(todayAdapter);
        recyclerSuggest.setAdapter(suggestAdapter);

        setupTabs();

        swipeRefreshLayout.setOnRefreshListener(() -> fetchNews(currentRssUrl));

        fetchNews(currentRssUrl);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_home)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_news)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_world)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_sports)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_tech)));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currentRssUrl = "https://thanhnien.vn/rss/home.rss"; break;
                    case 1: currentRssUrl = "https://thanhnien.vn/rss/thoi-su.rss"; break;
                    case 2: currentRssUrl = "https://thanhnien.vn/rss/the-gioi.rss"; break;
                    case 3: currentRssUrl = "https://thanhnien.vn/rss/the-thao.rss"; break;
                    case 4: currentRssUrl = "https://thanhnien.vn/rss/cong-nghe.rss"; break;
                }
                articleList.clear();
                splitAndDisplayArticles(articleList);
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
                    splitAndDisplayArticles(articleList);
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

    private void splitAndDisplayArticles(List<Article> sourceList) {
        todayList.clear();
        suggestList.clear();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayStr = sdf.format(new Date());

        for (Article article : sourceList) {
            String pubDate = article.getPubDate();
            if (pubDate != null && pubDate.startsWith(todayStr)) {
                todayList.add(article);
            } else {
                suggestList.add(article);
            }
        }

        if (todayList.size() > 5) {
            suggestList.addAll(0, todayList.subList(5, todayList.size()));
            todayList = new ArrayList<>(todayList.subList(0, 5));
        }

        boolean hasTodayNews = !todayList.isEmpty();
        tvTitleToday.setVisibility(hasTodayNews ? View.VISIBLE : View.GONE);
        recyclerToday.setVisibility(hasTodayNews ? View.VISIBLE : View.GONE);

        todayAdapter.notifyDataSetChanged();
        suggestAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem favItem = menu.add("Yêu thích").setIcon(android.R.drawable.ic_menu_add);
        favItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        favItem.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
            return true;
        });

        MenuItem historyItem = menu.add("Lịch sử").setIcon(android.R.drawable.ic_menu_agenda);
        historyItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        historyItem.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(MainActivity.this, ReadingHistoryActivity.class));
            return true;
        });

        MenuItem statsItem = menu.add("Thống kê").setIcon(android.R.drawable.ic_menu_info_details);
        statsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        statsItem.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(MainActivity.this, StatsActivity.class));
            return true;
        });

        MenuItem settingsItem = menu.add("Cài đặt").setIcon(android.R.drawable.ic_menu_preferences);
        settingsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        settingsItem.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        });

        MenuItem langItem = menu.add("Language").setIcon(android.R.drawable.ic_menu_mapmode);
        langItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        langItem.setOnMenuItemClickListener(item -> {
            androidx.core.os.LocaleListCompat currentLocale = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales();
            if (currentLocale.isEmpty() || currentLocale.get(0).getLanguage().equals("vi")) {
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(androidx.core.os.LocaleListCompat.forLanguageTags("en"));
            } else {
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(androidx.core.os.LocaleListCompat.forLanguageTags("vi"));
            }
            return true;
        });

        MenuItem aboutItem = menu.add("Giới thiệu").setIcon(android.R.drawable.ic_menu_help);
        aboutItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        aboutItem.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        });

        MenuItem searchItem = menu.add(getString(R.string.search)).setIcon(android.R.drawable.ic_menu_search);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        SearchView searchView = new SearchView(this);
        searchItem.setActionView(searchView);
        searchView.setQueryHint(getString(R.string.search_hint));

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
                splitAndDisplayArticles(filteredList);
                return true;
            }
        });
        return true;
    }
}

