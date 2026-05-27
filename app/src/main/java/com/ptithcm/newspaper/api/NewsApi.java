package com.ptithcm.newspaper.api;

import com.ptithcm.newspaper.model.RssResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {
    // Đường dẫn API của rss2json
    @GET("v1/api.json")
    Call<RssResponse> getNewsList(@Query("rss_url") String rssUrl);
}