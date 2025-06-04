package com.chattime.chattime_api.dto.response.channel;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.service.UserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
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

    public static List<ChannelDataResponse> fromList(List<Channel> channels) {
        return channels.stream().map(channel -> new ChannelDataResponse(
            channel.getId(),
            channel.getKey(),
            channel.getName(),
            channel.getStatus(),
            channel.getUser()
        )).collect(Collectors.toList());
    }
}
