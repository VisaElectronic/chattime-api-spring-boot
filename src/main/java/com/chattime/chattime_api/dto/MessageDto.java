package com.chattime.chattime_api.dto;

public class MessageDto {
    private String content;
    private Long channelId;
    private Long userId;

    public MessageDto() {
    }

    public MessageDto(String content, Long channelId, Long userId) {
        this.content = content;
        this.channelId = channelId;
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
