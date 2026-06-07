package com.ptithcm.newspaper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.data.model.User;
import com.ptithcm.newspaper.data.remote.BackendApiClient;
import com.ptithcm.newspaper.data.remote.UserApi;
import com.ptithcm.newspaper.util.PreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PremiumActivity extends AppCompatActivity {
    private Button btnBuyPremium;
    private TextView tvCancel;
    private PreferencesManager prefs;
    private long currentOrderCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        prefs = new PreferencesManager(this);

        btnBuyPremium = findViewById(R.id.btnBuyPremium);
        tvCancel = findViewById(R.id.tvCancel);

        tvCancel.setOnClickListener(v -> finish());

        btnBuyPremium.setOnClickListener(v -> buyPremium());
    }

    private void buyPremium() {
        User user = prefs.getCurrentUser();
        if (user == null) return;

        btnBuyPremium.setEnabled(false);
        btnBuyPremium.setText("Đang tạo link thanh toán PayOS...");

        UserApi userApi = BackendApiClient.getClient().create(UserApi.class);
        userApi.createPayosLink(new UserApi.UserIdRequest(user.getId())).enqueue(new retrofit2.Callback<UserApi.PayosCreateResponse>() {
            @Override
            public void onResponse(retrofit2.Call<UserApi.PayosCreateResponse> call, retrofit2.Response<UserApi.PayosCreateResponse> response) {
                btnBuyPremium.setEnabled(true);
                btnBuyPremium.setText("Mua Gói Premium (50.000 đ)");

                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    String checkoutUrl = response.body().checkoutUrl;
                    currentOrderCode = response.body().orderCode;
                    Intent intent = new Intent(PremiumActivity.this, PayosActivity.class);
                    intent.putExtra("CHECKOUT_URL", checkoutUrl);
                    startActivityForResult(intent, 100);
                } else {
                    Toast.makeText(PremiumActivity.this, "Lỗi tạo link thanh toán", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<UserApi.PayosCreateResponse> call, Throwable t) {
                btnBuyPremium.setEnabled(true);
                btnBuyPremium.setText("Mua Gói Premium (50.000 đ)");
                Toast.makeText(PremiumActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            User user = prefs.getCurrentUser();
            if (user != null && currentOrderCode != -1) {
                UserApi userApi = BackendApiClient.getClient().create(UserApi.class);
                userApi.checkPaymentStatus(new UserApi.OrderCheckRequest(user.getId(), currentOrderCode)).enqueue(new retrofit2.Callback<UserApi.OrderCheckResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<UserApi.OrderCheckResponse> call, retrofit2.Response<UserApi.OrderCheckResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().success && response.body().is_premium) {
                            user.setPremium(true);
                            prefs.saveCurrentUser(user);
                            Toast.makeText(PremiumActivity.this, "Thanh toán thành công! Bạn đã là VIP", Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(retrofit2.Call<UserApi.OrderCheckResponse> call, Throwable t) {
                        finish();
                    }
                });
            } else {
                finish();
            }
        }
    }
}
