package com.chattime.chattime_api.dto;

public class SocketToken {
    private String authToken;

    public SocketToken() {
    }

    public SocketToken(String token) {
        this.authToken = token;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String auth_token) {
        this.authToken = auth_token;
    }
}
