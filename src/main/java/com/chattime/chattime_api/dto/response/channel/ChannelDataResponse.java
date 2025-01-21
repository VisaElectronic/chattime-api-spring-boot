package com.chattime.chattime_api.dto.response.channel;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelDataResponse {
    private Long id;
    private String key;
    private String name;
    private int status;
    private boolean isGroup = false;
    private User user;

    @Autowired
    private UserService userService;

    public ChannelDataResponse(
            Long id,
            String key,
            String name,
            int status,
            User user
    ) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.status = status;
        this.user = user;
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
            channel.getName(),
            channel.getStatus(),
            channel.getUser()
        )).collect(Collectors.toList());
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
