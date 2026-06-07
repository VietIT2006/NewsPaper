package com.ptithcm.newspaper.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

public class MomoPaymentActivity extends AppCompatActivity {
    private Button btnConfirmPayment;
    private TextView tvCancelPayment;
    private android.widget.ImageView ivQrCode;
    private PreferencesManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_momo_payment);

        prefs = new PreferencesManager(this);

        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        tvCancelPayment = findViewById(R.id.tvCancelPayment);
        ivQrCode = findViewById(R.id.ivQrCode);

        // Chuỗi dữ liệu định dạng mã QR của MoMo
        // 2|99|<SĐT>|<TÊN>|<EMAIL>|0|0|<SỐ TIỀN>|<LỜI NHẮN>
        String qrData = "2|99|0365113154|MAI SON VIET||0|0|50000|Thanh toan VIP NewsApp";
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=500x500&data=" + android.net.Uri.encode(qrData);

        // Load ảnh QR từ URL bằng Glide
        com.bumptech.glide.Glide.with(this)
                .load(qrUrl)
                .into(ivQrCode);

        tvCancelPayment.setOnClickListener(v -> finish());

        btnConfirmPayment.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        User user = prefs.getCurrentUser();
        if (user == null) return;

        btnConfirmPayment.setEnabled(false);
        btnConfirmPayment.setText("Đang kiểm tra giao dịch...");

        // Giả lập thời gian chờ xử lý từ hệ thống ngân hàng (2 giây)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            UserApi userApi = BackendApiClient.getClient().create(UserApi.class);
            userApi.buyPremium(new UserApi.UserIdRequest(user.getId())).enqueue(new Callback<UserApi.SimpleResponse>() {
                @Override
                public void onResponse(Call<UserApi.SimpleResponse> call, Response<UserApi.SimpleResponse> response) {
                    btnConfirmPayment.setEnabled(true);
                    btnConfirmPayment.setText("Xác Nhận Đã Chuyển Khoản");

                    if (response.isSuccessful() && response.body() != null && response.body().success) {
                        Toast.makeText(MomoPaymentActivity.this, "Thanh toán thành công! Bạn đã lên gói Premium.", Toast.LENGTH_LONG).show();
                        user.setPremium(true);
                        prefs.saveCurrentUser(user);
                        
                        // Đóng màn hình này và quay lại
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(MomoPaymentActivity.this, "Giao dịch thất bại, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserApi.SimpleResponse> call, Throwable t) {
                    btnConfirmPayment.setEnabled(true);
                    btnConfirmPayment.setText("Xác Nhận Đã Chuyển Khoản");
                    Toast.makeText(MomoPaymentActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }, 2000);
    }
}
