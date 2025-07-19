package com.chattime.chattime_api.dto.response.channel;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.Group;
import com.chattime.chattime_api.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class GroupDataResponse {
    private Long id;
    private String name;
    private String customFirstname;
    private String customLastname;
    private String photo;
    private String key;
    private int status;
    private boolean isGroup;
    private ChannelData channel;
    private List<ChannelData> channels;

    public GroupDataResponse(
            Long id,
            String name,
            String customFirstname,
            String customLastname,
            String photo,
            String key,
            int status,
            boolean isGroup,
            ChannelData channel,
            List<ChannelData> channels
    ) {
        this.id = id;
        this.name = name;
        this.customFirstname = customFirstname;
        this.customLastname = customLastname;
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
            List<ChannelData> channelDtos = channels.stream()
                    .map(ch -> new ChannelData(
                            ch.getId(),
                            ch.getKey(),
                            ch.getName(),
                            ch.getUser()
                    ))
                    .toList();
            ChannelData recipientChannel = null;
            if(!group.isGroup()) {
                recipientChannel = channels.stream()
                    .map(ch -> new ChannelData(
                            ch.getId(),
                            ch.getKey(),
                            ch.getName(),
                            ch.getUser()
                    ))
                    .filter(item -> !item.getKey().equals(currentUser.getKey()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(
                            "No recipient channel found for user " + currentUser.getKey()
                    ));
            }
            return new GroupDataResponse(
                    group.getId(),
                    group.getName(),
                    group.getCustomFirstname(),
                    group.getCustomLastname(),
                    group.getPhoto(),
                    group.getKey(),
                    group.getStatus(),
                    group.isGroup(),
                    recipientChannel,
                    channelDtos
            );
        }).toList();
    }

    public static List<GroupDataResponse> from(Group group, User currentUser, List<Channel> channels) {
        ChannelData recipientChannel = null;
        List<ChannelData> channelDtos = channels.stream()
                .map(ch -> new ChannelData(
                        ch.getId(),
                        ch.getKey(),
                        ch.getName(),
                        ch.getUser()
                ))
                .toList();
        if(!group.isGroup()) {
            recipientChannel = channels.stream()
                    .map(ch -> new ChannelData(
                            ch.getId(),
                            ch.getKey(),
                            ch.getName(),
                            ch.getUser()
                    ))
                    .filter(item -> !item.getKey().equals(currentUser.getKey()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(
                            "No recipient channel found for user " + currentUser.getKey()
                    ));
        }
        return List.of(new GroupDataResponse(
                group.getId(),
                group.getName(),
                group.getCustomFirstname(),
                group.getCustomLastname(),
                group.getPhoto(),
                group.getKey(),
                group.getStatus(),
                group.isGroup(),
                null,
                channelDtos
        ));
    }

}
