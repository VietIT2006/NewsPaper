package com.ptithcm.newspaper.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RssResponse {
    // API rss2json trả về danh sách bài viết nằm trong mảng "items"
    @SerializedName("items")
    private List<Article> items;

    public List<Article> getItems() { return items; }
    public void setItems(List<Article> items) { this.items = items; }
}