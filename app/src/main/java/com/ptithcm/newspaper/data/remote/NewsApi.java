package com.ptithcm.newspaper.data.remote;

import com.ptithcm.newspaper.data.model.RssResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface NewsApi {
    @GET
    Call<RssResponse> getNewsList(@Url String url);
}

