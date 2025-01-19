package com.chattime.chattime_api.dto;

public class MessageDto {
    private String content;
    private String channelId;
    private String type;

    public MessageDto() {
    }

    public MessageDto(String content, String channelId, String type) {
        this.content = content;
        this.channelId = channelId;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
