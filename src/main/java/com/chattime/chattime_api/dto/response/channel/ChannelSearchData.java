package com.chattime.chattime_api.dto.response.channel;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.Group;
import com.chattime.chattime_api.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class ChannelSearchData {
    Long   id;
    String name;
    User user;

    public ChannelSearchData(
            Long id,
            String name,
            User user
    ) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    public static ChannelSearchData from(Channel c) {
        return new ChannelSearchData(
                c.getId(),
                c.getName(),
                c.getCreatedBy()
        );
    }

    public static List<Channel> fromGroup(List<Group> groups, User currentUser) {
        // preserve insertion order
        Map<String, Channel> uniqueByKey = new LinkedHashMap<>();

        for (Group group : groups) {
            for (Channel channel : group.getChannels()) {
                // skip if createdBy matches current user
                if (channel.getCreatedBy() != null
                        && channel.getCreatedBy().getId().equals(currentUser.getId())) {
                    continue;
                }
                // insert only if the key isn't already present
                uniqueByKey.putIfAbsent(channel.getKey(), channel);
            }
        }

        // return the deduped, filtered channels as a list
        return new ArrayList<>(uniqueByKey.values());
    }

}

