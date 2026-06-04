package com.ptithcm.newspaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAbout);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Về ứng dụng");
        }

        TextView tvAppVersion = findViewById(R.id.tvAppVersion);
        TextView tvAppDescription = findViewById(R.id.tvAppDescription);
        TextView tvDeveloper = findViewById(R.id.tvDeveloper);

        // Thiết lập thông tin
        tvAppVersion.setText("Siêu Báo 24h - Phiên bản 1.0");
        tvAppDescription.setText(
            "Ứng dụng đọc tin tức hiện đại với các tính năng:\n\n" +
            "✓ Đọc tin tức từ nhiều chuyên mục\n" +
            "✓ Lưu bài viết yêu thích\n" +
            "✓ Xem lịch sử đọc\n" +
            "✓ Chế độ tối (Dark Mode)\n" +
            "✓ Tìm kiếm nâng cao\n" +
            "✓ Chia sẻ bài viết\n" +
            "✓ Xem thống kê đọc\n\n" +
            "Cảm ơn bạn đã sử dụng ứng dụng của chúng tôi!"
        );
        tvDeveloper.setText("Phát triển bởi: PTIT HCM Development Team");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

