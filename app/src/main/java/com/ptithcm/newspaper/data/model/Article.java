package com.ptithcm.newspaper.data.model;

import com.google.gson.annotations.SerializedName;

public class Article {
    @SerializedName("title")
    private String title;

    @SerializedName("link")
    private String link;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("description")
    private String description;
    
    @SerializedName("pubDate")
    private String pubDate;

    // New fields for advanced features
    private String note;           // Bookmark note
    private String tags;           // Comma-separated tags
    private String sentiment;      // "positive", "negative", "neutral"
    private String category;       // Category name for stats
    private String savedHtml;      // Saved HTML for offline reading
    private String sourceName;     // RSS source name

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
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSavedHtml() {
        return savedHtml;
    }

    public void setSavedHtml(String savedHtml) {
        this.savedHtml = savedHtml;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
