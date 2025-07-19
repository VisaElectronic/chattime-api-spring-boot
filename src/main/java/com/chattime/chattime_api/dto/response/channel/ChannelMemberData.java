package com.chattime.chattime_api.dto.response.channel;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.GroupChannel;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.service.UserService;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
public class ChannelMemberData extends ChannelData {
    private Integer role;

    @Autowired
    private UserService userService;

    public ChannelMemberData(
            Long id,
            String key,
            String name,
            User user,
            @Nullable
            Integer role
    ) {
        super(id, key, name, user);
        this.role = role;
    }

    public static ChannelMemberData fromMember(GroupChannel gc) {
        Channel c = gc.getChannel();
        return new ChannelMemberData(
                c.getId(),
                c.getKey(),
                c.getName(),
                c.getCreatedBy(),
                gc.getRole()
        );
    }

    public static ChannelMemberData from(Channel c) {
        return new ChannelMemberData(
                c.getId(),
                c.getKey(),
                c.getName(),
                c.getCreatedBy(),
                null
        );
    }
}
