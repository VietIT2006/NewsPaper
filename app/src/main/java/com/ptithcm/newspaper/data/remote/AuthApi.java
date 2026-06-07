package com.ptithcm.newspaper.data.remote;

import com.ptithcm.newspaper.data.model.AuthRequest;
import com.ptithcm.newspaper.data.model.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @POST("api/auth/register")
    Call<AuthResponse> register(@Body AuthRequest request);
}
