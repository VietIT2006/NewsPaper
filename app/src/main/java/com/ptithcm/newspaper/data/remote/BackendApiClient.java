package com.ptithcm.newspaper.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackendApiClient {
    // Sử dụng 10.0.2.2 thay cho localhost khi chạy trên Android Emulator
    // Nếu chạy trên máy thật cùng mạng Wifi, cần đổi thành IP LAN (ví dụ 192.168.1.x)
    private static final String BASE_URL = "http://192.168.1.48:3000/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
