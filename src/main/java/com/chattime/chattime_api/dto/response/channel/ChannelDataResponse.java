package com.chattime.chattime_api.dto.response.channel;

import com.chattime.chattime_api.model.Channel;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelDataResponse {
    private Long id;
    private String key;
    private String name;

    public ChannelDataResponse(Long id, String key, String name) {
        this.id = id;
        this.key = key;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<ChannelDataResponse> fromList(List<Channel> channels) {
        return channels.stream().map(channel -> new ChannelDataResponse(
            channel.getId(),
            channel.getKey(),
            channel.getName()
        )).collect(Collectors.toList());
    }
}
