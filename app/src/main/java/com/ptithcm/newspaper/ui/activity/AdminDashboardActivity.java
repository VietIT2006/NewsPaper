package com.ptithcm.newspaper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.data.model.User;
import com.ptithcm.newspaper.util.PreferencesManager;

public class AdminDashboardActivity extends AppCompatActivity {
    private TextView tvAdminGreeting;
    private Button btnManageNews, btnReadNews, btnLogout;
    private PreferencesManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        prefs = new PreferencesManager(this);
        User user = prefs.getCurrentUser();

        tvAdminGreeting = findViewById(R.id.tvAdminGreeting);
        btnManageNews = findViewById(R.id.btnManageNews);
        btnReadNews = findViewById(R.id.btnReadNews);
        btnLogout = findViewById(R.id.btnLogout);

        if (user != null) {
            tvAdminGreeting.setText("Xin chào Admin: " + user.getUsername());
        }

        btnManageNews.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
            // Tương lai: startActivity(new Intent(this, SourceManagerActivity.class));
        });

        btnReadNews.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, MainActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            prefs.logout();
            startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
            finishAffinity();
        });
    }
}
