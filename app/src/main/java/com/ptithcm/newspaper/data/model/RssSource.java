package com.ptithcm.newspaper.data.model;

public class RssSource {
    private int id;
    private String name;
    private String url;
    @com.google.gson.annotations.SerializedName("is_enabled")
    private boolean enabled;

    public RssSource(int id, String name, String url, boolean enabled) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.enabled = enabled;
    }

    public int getId() { return id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
