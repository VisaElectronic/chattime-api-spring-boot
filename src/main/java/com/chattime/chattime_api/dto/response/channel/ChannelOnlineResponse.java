package com.chattime.chattime_api.dto.response.channel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChannelOnlineResponse {
    private String type;
    private List<GroupDataResponse> groups;

    public ChannelOnlineResponse(
        String type,
        List<GroupDataResponse> groups
    ) {
        this.type = type;
        this.groups = groups;
    }
}
