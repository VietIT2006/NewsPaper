package com.ptithcm.newspaper.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Worker kiểm tra tin tức mới định kỳ.
 * Gọi API RSS và so sánh với bài viết cuối cùng đã biết.
 * Nếu có bài mới, gửi thông báo qua NotificationHelper.
 */
public class NewsCheckWorker extends Worker {

    private static final String PREFS_NAME = "LAST_NEWS_CHECK";
    private static final String KEY_LAST_LINK = "last_article_link";
    private static final String RSS_API_URL =
            "https://api.rss2json.com/v1/api.json?rss_url=https://thanhnien.vn/rss/home.rss";

    public NewsCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            URL url = new URL(RSS_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return Result.retry();
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            conn.disconnect();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray items = jsonResponse.getJSONArray("items");

            if (items.length() > 0) {
                JSONObject firstItem = items.getJSONObject(0);
                String articleTitle = firstItem.getString("title");
                String articleLink = firstItem.getString("link");

                // So sánh với bài viết cuối cùng đã biết
                SharedPreferences prefs = getApplicationContext()
                        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String lastLink = prefs.getString(KEY_LAST_LINK, "");

                if (!articleLink.equals(lastLink)) {
                    // Có bài viết mới -> gửi thông báo
                    NotificationHelper.showNewsNotification(
                            getApplicationContext(), articleTitle, articleLink);

                    // Lưu link bài viết mới
                    prefs.edit().putString(KEY_LAST_LINK, articleLink).apply();
                }
            }

            return Result.success();

        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}
