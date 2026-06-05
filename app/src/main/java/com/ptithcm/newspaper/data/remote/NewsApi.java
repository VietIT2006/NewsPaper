package com.ptithcm.newspaper.data.remote;

import com.ptithcm.newspaper.data.model.RssResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {
    @GET("api.json")
    Call<RssResponse> getNewsList(@Query("rss_url") String rssUrl);
}