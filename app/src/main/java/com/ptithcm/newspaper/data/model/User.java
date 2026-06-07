package com.ptithcm.newspaper.data.model;

import com.google.gson.annotations.SerializedName;

public class User {
    private int id;
    private String username;
    private String role;
    
    @SerializedName("is_premium")
    private boolean isPremium;
    
    @SerializedName("free_uses_left")
    private int freeUsesLeft;

    public User(int id, String username, String role, boolean isPremium, int freeUsesLeft) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.isPremium = isPremium;
        this.freeUsesLeft = freeUsesLeft;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public boolean isPremium() { return isPremium; }
    public void setPremium(boolean premium) { isPremium = premium; }
    public int getFreeUsesLeft() { return freeUsesLeft; }
    public void setFreeUsesLeft(int freeUsesLeft) { this.freeUsesLeft = freeUsesLeft; }
}
