package com.ptithcm.newspaper.data.model;

public class AuthRequest {
    private String username;
    private String password;
    private String role;

    public AuthRequest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = "user";
    }
}
