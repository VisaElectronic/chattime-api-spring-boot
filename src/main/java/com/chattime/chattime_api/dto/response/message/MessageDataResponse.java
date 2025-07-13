package com.chattime.chattime_api.dto.response.message;

import com.chattime.chattime_api.dto.response.channel.GroupDataResponse;
import com.chattime.chattime_api.model.Group;
import com.chattime.chattime_api.model.Message;
import com.chattime.chattime_api.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
public class MessageDataResponse {
    private Long id;
    private String content;
    private Date createdAt;
    private GroupDataResponse group;
    private User user;

    public MessageDataResponse() {
    }

    public MessageDataResponse(
            Long id,
            String content,
            Group group,
            User user,
            Date createdAt
    ) {
        this.id = id;
        this.content = content;
        this.group = new GroupDataResponse(
                group.getId(),
                group.getName(),
                group.getCustomFirstname(),
                group.getCustomLastname(),
                group.getPhoto(),
                group.getKey(),
                group.getStatus(),
                group.isGroup(),
                null,
                null
        );
        this.user = user;
        this.createdAt = createdAt;
    }

    public static List<MessageDataResponse> fromList(List<Message> messages, User currentLoginUser) {
        return messages.stream().map(message -> {
            User sender = message.getCreatedBy();
            return new MessageDataResponse(
                    message.getId(),
                    message.getContent(),
                    message.getGroup(),
                    sender,
                    message.getCreatedAt()
            );
        }).toList();
    }

}
