package com.chattime.chattime_api.dto.socket;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConnectOnlineDto {
    private String userId;
    private String channelId;

    public ConnectOnlineDto() {
    }

    public ConnectOnlineDto(String userId, String channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }

}
