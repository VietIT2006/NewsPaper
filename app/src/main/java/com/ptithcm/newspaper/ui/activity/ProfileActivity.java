package com.ptithcm.newspaper.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.data.model.AuthResponse;
import com.ptithcm.newspaper.data.model.User;
import com.ptithcm.newspaper.data.remote.BackendApiClient;
import com.ptithcm.newspaper.data.remote.UserApi;
import com.ptithcm.newspaper.util.PreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvAccountType, tvFreeUses;
    private Button btnUpgradeVip, btnLogout;
    private LinearLayout layoutProfileBg;
    private PreferencesManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Hồ sơ cá nhân");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        prefs = new PreferencesManager(this);

        tvUsername = findViewById(R.id.tvUsername);
        tvAccountType = findViewById(R.id.tvAccountType);
        tvFreeUses = findViewById(R.id.tvFreeUses);
        btnUpgradeVip = findViewById(R.id.btnUpgradeVip);
        btnLogout = findViewById(R.id.btnLogout);
        layoutProfileBg = findViewById(R.id.layoutProfileBg);

        btnLogout.setOnClickListener(v -> {
            prefs.logout();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finishAffinity();
        });

        btnUpgradeVip.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, PremiumActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        User user = prefs.getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        // Hiện dữ liệu local trước cho mượt
        updateUI(user);

        // Fetch dữ liệu mới nhất từ server
        UserApi userApi = BackendApiClient.getClient().create(UserApi.class);
        userApi.getProfile(user.getId()).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User updatedUser = response.body().getUser();
                    if (updatedUser != null) {
                        prefs.saveCurrentUser(updatedUser);
                        updateUI(updatedUser);
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                // Ignore network error, keep using local data
            }
        });
    }

    private void updateUI(User user) {
        tvUsername.setText(user.getUsername());
        
        if (user.isPremium()) {
            tvAccountType.setText(" ⭐ TÀI KHOẢN PREMIUM VIP ");
            tvAccountType.setBackgroundColor(Color.parseColor("#FFD700")); // Vàng gold
            tvAccountType.setTextColor(Color.BLACK);
            layoutProfileBg.setBackgroundColor(Color.parseColor("#FFFDE7")); // Nền vàng nhạt
            
            tvFreeUses.setVisibility(View.GONE);
            btnUpgradeVip.setVisibility(View.GONE);
        } else {
            tvAccountType.setText("TÀI KHOẢN THƯỜNG");
            tvAccountType.setBackgroundColor(Color.parseColor("#888888"));
            tvAccountType.setTextColor(Color.WHITE);
            layoutProfileBg.setBackgroundColor(Color.TRANSPARENT);

            tvFreeUses.setVisibility(View.VISIBLE);
            tvFreeUses.setText("Lượt đọc bằng AI miễn phí còn lại: " + user.getFreeUsesLeft() + "/3");
            btnUpgradeVip.setVisibility(View.VISIBLE);
        }
    }
}
