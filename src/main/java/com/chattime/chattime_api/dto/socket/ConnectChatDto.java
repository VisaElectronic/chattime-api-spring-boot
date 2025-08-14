package com.chattime.chattime_api.dto.socket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ConnectChatDto {
    private int limit = 10;
    private int offset = 0;

    public ConnectChatDto(
        int limit,
        int offset
    ) {
        this.limit = limit;
        this.offset = offset;
    }

}
