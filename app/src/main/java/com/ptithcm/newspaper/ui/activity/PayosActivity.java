package com.ptithcm.newspaper.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ptithcm.newspaper.R;

public class PayosActivity extends AppCompatActivity {
    private WebView webView;
    private String checkoutUrl;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payos);

        Toolbar toolbar = findViewById(R.id.toolbarPayos);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thanh toán an toàn PayOS");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        webView = findViewById(R.id.webViewPayos);
        checkoutUrl = getIntent().getStringExtra("CHECKOUT_URL");

        if (checkoutUrl == null || checkoutUrl.isEmpty()) {
            Toast.makeText(this, "Lỗi tạo đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("payos-return")) {
                    // Cho người dùng xem thông báo 2 giây rồi tự động đóng trang web
                    view.postDelayed(() -> {
                        setResult(RESULT_OK);
                        finish();
                    }, 2000);
                } else if (url.contains("payos-cancel")) {
                    view.postDelayed(() -> {
                        setResult(RESULT_CANCELED);
                        finish();
                    }, 2000);
                }
            }
        });

        webView.loadUrl(checkoutUrl);
    }
}
