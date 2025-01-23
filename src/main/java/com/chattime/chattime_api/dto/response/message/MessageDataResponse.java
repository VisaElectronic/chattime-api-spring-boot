package com.chattime.chattime_api.dto.response.message;

import com.chattime.chattime_api.dto.response.channel.GroupDataResponse;
import com.chattime.chattime_api.model.Group;
import com.chattime.chattime_api.model.Message;
import com.chattime.chattime_api.model.User;

import java.util.Date;
import java.util.List;

public class MessageDataResponse {
    private Long id;
    private String content;
    private Date createdAt;
    private GroupDataResponse group;
    private User user;

    public MessageDataResponse() {
    }

    public MessageDataResponse(Long id, String content, Group group, User user, Date createdAt) {
        this.id = id;
        this.content = content;
        this.group = new GroupDataResponse(
                group.getId(),
                group.getKey(),
                null
        );
        this.user = user;
        this.createdAt = createdAt;
    }

    public static List<MessageDataResponse> fromList(List<Message> messages) {
        return messages.stream().map(message -> new MessageDataResponse(
                message.getId(),
                message.getContent(),
                message.getGroup(),
                message.getCreatedBy(),
                message.getCreatedAt()
        )).toList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public GroupDataResponse getGroup() {
        return group;
    }

    public void setGroup(GroupDataResponse group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
