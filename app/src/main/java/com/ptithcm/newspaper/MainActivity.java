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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.ptithcm.newspaper.adapter.ArticleAdapter;
import com.ptithcm.newspaper.api.ApiClient;
import com.ptithcm.newspaper.api.NewsApi;
import com.ptithcm.newspaper.model.Article;
import com.ptithcm.newspaper.model.RssResponse;

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
    private List<Article> articleList; // Danh sách gốc chứa toàn bộ
    private List<Article> todayList, suggestList; // 2 danh sách phân loại
    private String currentRssUrl = "https://thanhnien.vn/rss/home.rss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo Toolbar
        Toolbar toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);

        // Ánh xạ UI
        recyclerToday = findViewById(R.id.recyclerToday);
        recyclerSuggest = findViewById(R.id.recyclerSuggest);
        tvTitleToday = findViewById(R.id.tvTitleToday);
        tvTitleSuggest = findViewById(R.id.tvTitleSuggest);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tabLayout = findViewById(R.id.tabLayout);

        // Cấu hình LayoutManager
        recyclerToday.setLayoutManager(new LinearLayoutManager(this));
        recyclerSuggest.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo danh sách và Adapter
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
                splitAndDisplayArticles(articleList); // Làm rỗng giao diện khi đổi tab
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
                    // Gọi hàm phân loại hiển thị
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

    // --- THUẬT TOÁN PHÂN LOẠI TIN TỨC ---
    private void splitAndDisplayArticles(List<Article> sourceList) {
        todayList.clear();
        suggestList.clear();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayStr = sdf.format(new Date()); // Lấy ngày hiện tại (VD: 2026-05-27)

        for (Article article : sourceList) {
            String pubDate = article.getPubDate();
            // Nếu ngày đăng bài trùng với chuỗi ngày hiện tại -> Đưa vào danh sách Hôm nay
            if (pubDate != null && pubDate.startsWith(todayStr)) {
                todayList.add(article);
            } else {
                suggestList.add(article);
            }
        }

        // Để tránh khu vực Hôm nay quá dài, ta chỉ giữ 5 bài mới nhất, phần dư đẩy xuống Gợi ý
        if (todayList.size() > 5) {
            suggestList.addAll(0, todayList.subList(5, todayList.size()));
            todayList = new ArrayList<>(todayList.subList(0, 5));
        }

        // Nếu không có tin nào trong hôm nay, tự động ẩn khu vực đó đi
        boolean hasTodayNews = !todayList.isEmpty();
        tvTitleToday.setVisibility(hasTodayNews ? View.VISIBLE : View.GONE);
        recyclerToday.setVisibility(hasTodayNews ? View.VISIBLE : View.GONE);

        // Cập nhật giao diện
        todayAdapter.notifyDataSetChanged();
        suggestAdapter.notifyDataSetChanged();
    }

    // --- TÍNH NĂNG TÌM KIẾM ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 1. TẠO NÚT ĐỔI NGÔN NGỮ (LANGUAGE)
        MenuItem langItem = menu.add("Language").setIcon(android.R.drawable.ic_menu_mapmode);
        langItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        langItem.setOnMenuItemClickListener(item -> {
            // Kiểm tra ngôn ngữ hiện tại và đảo ngược lại
            androidx.core.os.LocaleListCompat currentLocale = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales();
            if (currentLocale.isEmpty() || currentLocale.get(0).getLanguage().equals("vi")) {
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(androidx.core.os.LocaleListCompat.forLanguageTags("en"));
            } else {
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(androidx.core.os.LocaleListCompat.forLanguageTags("vi"));
            }
            return true;
        });

        // 2. TẠO NÚT TÌM KIẾM (Đã gọi từ tệp strings.xml)
        MenuItem searchItem = menu.add(getString(R.string.search)).setIcon(android.R.drawable.ic_menu_search);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        SearchView searchView = new SearchView(this);
        searchItem.setActionView(searchView);

        // Gọi dòng gợi ý "Nhập tên bài báo..." từ tệp strings.xml
        searchView.setQueryHint(getString(R.string.search_hint));

        // 3. XỬ LÝ LỌC DANH SÁCH BÀI BÁO
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