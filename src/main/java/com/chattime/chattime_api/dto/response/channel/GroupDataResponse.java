package com.chattime.chattime_api.dto.response.channel;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupDataResponse {
    private Long id;
    private String key;
    private List<Channel> channels;

    public GroupDataResponse(
            Long id,
            String key,
            List<Channel> channels
    ) {
        this.id = id;
        this.key = key;
        this.channels = channels;
    }

    public static List<GroupDataResponse> fromList(List<Group> groups) {
        return groups.stream().map(group -> new GroupDataResponse(
                group.getId(),
                group.getKey(),
                new ArrayList<>(group.getChannels())
        )).toList();
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

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }
}
