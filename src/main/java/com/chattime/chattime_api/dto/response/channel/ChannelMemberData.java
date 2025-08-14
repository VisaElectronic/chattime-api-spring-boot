package com.chattime.chattime_api.dto.response.channel;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.GroupChannel;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.service.UserService;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChannelMemberData extends ChannelData {
    private Integer role;

    private String createdAt;

    @Autowired
    private UserService userService;

    public ChannelMemberData(
            Long id,
            String key,
            String name,
            User user,
            @Nullable
            Integer role,
            LocalDateTime createdAt
    ) {
        super(id, key, name, user);
        this.role = role;
        this.createdAt = createdAt != null ? createdAt.toString() : null;
    }

    public static ChannelMemberData fromMember(GroupChannel gc) {
        Channel c = gc.getChannel();
        return new ChannelMemberData(
                c.getId(),
                c.getKey(),
                c.getName(),
                c.getCreatedBy(),
                gc.getRole(),
                gc.getCreatedAt()
        );
    }

    public static ChannelMemberData from(Channel c) {
        return new ChannelMemberData(
                c.getId(),
                c.getKey(),
                c.getName(),
                c.getCreatedBy(),
                null,
                null
        );
    }
}
