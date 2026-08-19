package com.gestion.affectations.ui.service;

public class AuthContext {
    private static AuthContext instance;
    private String token;
    private String username;

    private AuthContext() {}

    public static AuthContext getInstance() {
        if (instance == null) {
            instance = new AuthContext();
        }
        return instance;
    }

    public void setSession(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public void logout() {
        this.token = null;
        this.username = null;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAuthenticated() {
        return token != null && !token.isEmpty();
    }
}
