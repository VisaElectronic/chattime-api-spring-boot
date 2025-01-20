package com.chattime.chattime_api.dto.socket;

public class AuthDto {
    private String authToken;

    public AuthDto() {
    }

    public AuthDto(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
