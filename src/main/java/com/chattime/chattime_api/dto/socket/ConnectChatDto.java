package com.chattime.chattime_api.dto.socket;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConnectChatDto {
    private Boolean isGroup = false;

    public ConnectChatDto(Boolean isGroup) {
        this.isGroup = isGroup;
    }

}
