package com.chattime.chattime_api.dto.response.login;

public class LoginDataResponse {
    private String access_token;

    public LoginDataResponse() {
    }

    public LoginDataResponse(String access_token) {
        this.access_token = access_token;
    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String token) {
        this.access_token = token;
    }
}
