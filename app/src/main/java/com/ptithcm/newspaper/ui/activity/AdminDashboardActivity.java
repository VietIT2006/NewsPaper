package com.ptithcm.newspaper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.data.model.User;
import com.ptithcm.newspaper.util.PreferencesManager;

public class AdminDashboardActivity extends AppCompatActivity {
    private TextView tvAdminGreeting;
    private CardView cardManageNews, cardReadNews, cardLogout, cardViewRevenue;
    private PreferencesManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        prefs = new PreferencesManager(this);
        User user = prefs.getCurrentUser();

        tvAdminGreeting = findViewById(R.id.tvAdminGreeting);
        cardManageNews = findViewById(R.id.cardManageNews);
        cardReadNews = findViewById(R.id.cardReadNews);
        cardLogout = findViewById(R.id.cardLogout);
        cardViewRevenue = findViewById(R.id.cardViewRevenue);

        if (user != null) {
            tvAdminGreeting.setText("Xin chào Admin: " + user.getUsername());
        }

        cardManageNews.setOnClickListener(v -> {
            startActivity(new Intent(this, SourceManagerActivity.class));
        });
        
        cardViewRevenue.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminRevenueActivity.class));
        });

        cardReadNews.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, MainActivity.class));
        });

        cardLogout.setOnClickListener(v -> {
            prefs.logout();
            startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
            finishAffinity();
        });
    }
}
