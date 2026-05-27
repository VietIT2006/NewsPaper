package com.ptithcm.newspaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {

    private WebView webViewContent;
    private ProgressBar progressBarDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết bài viết");
        }

        webViewContent = findViewById(R.id.webViewContent);
        progressBarDetail = findViewById(R.id.progressBarDetail);

        webViewContent.getSettings().setJavaScriptEnabled(true);
        webViewContent.getSettings().setDefaultTextEncodingName("utf-8"); // Sửa lỗi font chữ

        String articleUrl = getIntent().getStringExtra("ARTICLE_LINK");
        if (articleUrl != null) {
            scrapeArticleContent(articleUrl);
        }
    }

    // --- TÍNH NĂNG CHIA SẺ ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Chia sẻ").setIcon(android.R.drawable.ic_menu_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == 1) {
            String articleUrl = getIntent().getStringExtra("ARTICLE_LINK");
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Gửi bạn bài báo này:");
            shareIntent.putExtra(Intent.EXTRA_TEXT, articleUrl);
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua:"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void scrapeArticleContent(String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                Document document = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36")
                        .get();

                Element contentElement = document.selectFirst(".detail-cmain");
                if (contentElement == null) {
                    contentElement = document.selectFirst(".detail-content");
                }

                final String finalHtml = (contentElement != null) ? contentElement.html() : "Không thể bóc tách nội dung!";
                handler.post(() -> {
                    progressBarDetail.setVisibility(View.GONE);
                    if (!finalHtml.equals("Không thể bóc tách nội dung!")) {
                        webViewContent.setVisibility(View.VISIBLE);
                        String styledHtml = "<html><head><meta charset=\"UTF-8\"><style>img{max-width: 100%; height: auto;} body{padding: 10px; font-size: 16px;}</style></head><body>"
                                + finalHtml + "</body></html>";
                        webViewContent.loadDataWithBaseURL(url, styledHtml, "text/html; charset=utf-8", "UTF-8", null);
                    } else {
                        Toast.makeText(DetailActivity.this, finalHtml, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    progressBarDetail.setVisibility(View.GONE);
                    Toast.makeText(DetailActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}