package com.chattime.chattime_api.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AddGroupDto {
    private String title;
    private String profile;
    private List<String> channelKeys;

    public AddGroupDto() {
    }

    public AddGroupDto(
            String title,
            String profile,
            List<String> channelKeys
    ) {
        this.title = title;
        this.profile = profile;
        this.channelKeys = channelKeys;
    }

    public void addChannelKey(String key) {
        channelKeys.add(key);
    }
}
