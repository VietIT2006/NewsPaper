package com.ptithcm.newspaper.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RssResponse {
    @SerializedName("items")
    private List<Article> items;

    @SerializedName("status")
    private String status;

    public List<Article> getItems() {
        return items;
    }

    public void setItems(List<Article> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

