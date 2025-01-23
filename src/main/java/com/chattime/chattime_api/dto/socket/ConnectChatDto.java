package com.chattime.chattime_api.dto.socket;

public class ConnectChatDto {
    private Boolean isGroup = false;

    public ConnectChatDto(Boolean isGroup) {
        this.isGroup = isGroup;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(Boolean group) {
        isGroup = group;
    }
}
