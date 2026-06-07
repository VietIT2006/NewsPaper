package com.ptithcm.newspaper.data.remote;

import com.ptithcm.newspaper.data.model.AuthResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {

    // Lấy thông tin user
    @GET("api/user/profile/{id}")
    Call<AuthResponse> getProfile(@Path("id") int userId);

    // Báo cáo sử dụng tính năng
    @POST("api/user/use-feature")
    Call<FeatureUseResponse> useFeature(@Body UserIdRequest request);

    // Mua gói Premium (Cũ)
    @POST("api/payment/buy-premium")
    Call<SimpleResponse> buyPremium(@Body UserIdRequest request);

    // Tạo link thanh toán PayOS
    @POST("api/payment/payos-create")
    Call<PayosCreateResponse> createPayosLink(@Body UserIdRequest request);

    // Kiểm tra trạng thái đơn hàng PayOS
    @POST("api/payment/payos-check")
    Call<OrderCheckResponse> checkPaymentStatus(@Body OrderCheckRequest request);

    // Admin: Xem doanh thu
    @GET("api/admin/revenue")
    Call<RevenueResponse> getRevenue(@retrofit2.http.Query("date") String date);

    // Lấy danh sách nguồn tin
    @GET("api/sources")
    Call<SourceResponse> getSources();

    // Admin: Thêm nguồn tin mới
    @POST("api/admin/sources")
    Call<AuthResponse> addSource(@Body SourceRequest request);

    // Admin: Bật/Tắt nguồn tin
    @PUT("api/admin/sources/{id}")
    Call<AuthResponse> toggleSource(@Path("id") int id, @Body SourceToggleRequest request);

    // --- Helper classes for requests and responses ---

    public static class UserIdRequest {
        public int userId;
        public UserIdRequest(int userId) { this.userId = userId; }
    }

    public static class FeatureUseResponse {
        public boolean success;
        public String message;
        public int free_uses_left;
    }

    class SimpleResponse {
        public boolean success;
        public String message;
    }

    class PayosCreateResponse {
        public boolean success;
        public String checkoutUrl;
        public long orderCode;
    }

    public static class OrderCheckRequest {
        public int userId;
        public long orderCode;
        public OrderCheckRequest(int userId, long orderCode) { this.userId = userId; this.orderCode = orderCode; }
    }

    public static class OrderCheckResponse {
        public boolean success;
        public boolean is_premium;
    }

    public static class RevenueResponse {
        public boolean success;
        public long overall_revenue;
        public long total_revenue;
        public List<Transaction> transactions;
    }

    public static class Transaction {
        public int id;
        public int amount;
        public String description;
        public String created_at;
        public String username;
    }

    public static class SourceResponse {
        public boolean success;
        public List<com.ptithcm.newspaper.data.model.RssSource> sources;
    }

    public static class SourceRequest {
        public String name;
        public String url;
        public SourceRequest(String name, String url) { this.name = name; this.url = url; }
    }

    public static class SourceToggleRequest {
        public boolean is_enabled;
        public SourceToggleRequest(boolean is_enabled) { this.is_enabled = is_enabled; }
    }
}
