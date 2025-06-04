package com.chattime.chattime_api.dto.response.channel;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.Group;
import com.chattime.chattime_api.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
public class GroupDataResponse {
    private Long id;
    private String name;
    private String photo;
    private String key;
    private int status;
    private boolean isGroup;
    private Channel channel;
    private List<Channel> channels;

    public GroupDataResponse(
            Long id,
            String name,
            String photo,
            String key,
            int status,
            boolean isGroup,
            Channel channel,
            List<Channel> channels
    ) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.key = key;
        this.status = status;
        this.isGroup = isGroup;
        this.channel = channel;
        this.channels = channels;
    }

    public static List<GroupDataResponse> fromList(List<Group> groups, User currentUser) {
        return groups.stream().map(group -> {
            Set<Channel> channels = group.getChannels();
            Channel recipientChannel = null;
            if(!group.isGroup()) {
                recipientChannel = channels.stream()
                    .filter(item -> !item.getKey().equals(currentUser.getKey()))
                    .findFirst().orElseThrow();
            }
            return new GroupDataResponse(
                    group.getId(),
                    group.getName(),
                    group.getPhoto(),
                    group.getKey(),
                    group.getStatus(),
                    group.isGroup(),
                    recipientChannel,
                    new ArrayList<>(channels)
            );
        }).toList();
    }

}
