package com.chattime.chattime_api.dto.socket;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConnectChatDto {
    private int limit = 30;
    private int offset = 0;

    public ConnectChatDto(
        int limit,
        int offset
    ) {
        this.limit = limit;
        this.offset = offset;
    }

}
