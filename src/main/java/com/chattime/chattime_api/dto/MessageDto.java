package com.chattime.chattime_api.dto;

public class MessageDto {
    private String content;
    private String type;

    public MessageDto() {
    }

    public MessageDto(String content, String type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
