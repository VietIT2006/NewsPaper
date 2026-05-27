package com.ptithcm.newspaper; // Nhớ đổi tên package cho khớp dự án của bạn

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class DetailActivity extends AppCompatActivity {

    private WebView webViewContent;
    private ProgressBar progressBarDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Bật nút mũi tên quay lại
            getSupportActionBar().setTitle("Chi tiết bài viết");
        }

        webViewContent = findViewById(R.id.webViewContent);
        progressBarDetail = findViewById(R.id.progressBarDetail);
        webViewContent.getSettings().setJavaScriptEnabled(true);

        String articleUrl = getIntent().getStringExtra("ARTICLE_LINK");
        if (articleUrl != null) {
            scrapeArticleContent(articleUrl);
        }
    }

    // Bắt sự kiện khi bấm vào mũi tên quay lại trên Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng Activity hiện tại, quay về màn hình trước
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ... (Giữ nguyên hàm scrapeArticleContent của bạn ở đây)
    private void scrapeArticleContent(String url) {
        // (Sao chép lại nội dung hàm bóc tách HTML như cũ)
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                Document document = Jsoup.connect(url).get();
                Element contentElement = document.selectFirst(".detail-cmain");
                if (contentElement == null) {
                    contentElement = document.selectFirst(".detail-content");
                }
                final String finalHtml = (contentElement != null) ? contentElement.html() : "Không thể bóc tách nội dung!";
                handler.post(() -> {
                    progressBarDetail.setVisibility(View.GONE);
                    if (!finalHtml.equals("Không thể bóc tách nội dung!")) {
                        webViewContent.setVisibility(View.VISIBLE);
                        String styledHtml = "<html><head><style>img{max-width: 100%; height: auto;} body{padding: 10px; font-size: 16px;}</style></head><body>"
                                + finalHtml + "</body></html>";
                        webViewContent.loadDataWithBaseURL(url, styledHtml, "text/html", "UTF-8", null);
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