package com.chattime.chattime_api.dto.socket;

public class ConnectOnlineDto {
    private String userId;
    private String channelId;

    public ConnectOnlineDto() {
    }

    public ConnectOnlineDto(String userId, String channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
