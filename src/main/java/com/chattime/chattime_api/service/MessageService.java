package com.chattime.chattime_api.service;

import com.chattime.chattime_api.model.*;
import com.chattime.chattime_api.repository.ChannelRepository;
import com.chattime.chattime_api.repository.MessageRepository;
import com.chattime.chattime_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    public Message addMessage(Message message) {
        return messageRepository.save(message);
    }

    public User getUserFromSocketConnection(StompHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        UserPrincipal user = (UserPrincipal) Objects.requireNonNull(sessionAttributes).get("user");
        return user.getUser();
    }

    public List<Message> getMessagesByGroup(Group group, Pageable pg) {
        return messageRepository.paginateByGroupId(group.getId(), pg);
    }
}
