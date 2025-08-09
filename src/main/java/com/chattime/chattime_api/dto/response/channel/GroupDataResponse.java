package com.chattime.chattime_api.dto.response.channel;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.Group;
import com.chattime.chattime_api.model.Message;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.service.GroupService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

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
    private int unread;
    private Message lastMessage;

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
            List<ChannelData> channels,
            int unread,
            Message lastMessage
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
        this.unread = unread;
        this.lastMessage = lastMessage;
    }

}
