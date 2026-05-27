package com.ptithcm.newspaper.model; // Lưu ý: Đổi tên package này cho khớp với dự án của bạn nếu cần

import com.google.gson.annotations.SerializedName;

public class Article {
    @SerializedName("title")
    private String title;

    @SerializedName("link")
    private String link;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("pubDate")
    private String pubDate;

    // --- BẮT ĐẦU CÁC HÀM GETTER VÀ SETTER (PHẦN BẠN ĐANG THIẾU) ---

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    // --- KẾT THÚC CÁC HÀM GETTER VÀ SETTER ---
}