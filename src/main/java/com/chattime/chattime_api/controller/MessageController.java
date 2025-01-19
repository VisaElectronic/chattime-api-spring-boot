package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.MessageDto;
import com.chattime.chattime_api.model.*;
import com.chattime.chattime_api.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.chattime.chattime_api.service.MessageService;

import java.util.Objects;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChannelService channelService;

    @MessageMapping("/channel/{id}/chat")
    @SendTo("/channel/{id}/subscribe")
    public Message chatSocket(
            @DestinationVariable String id,
            MessageDto messageDto,
            StompHeaderAccessor headerAccessor
    ) {
        SocketUserPrincipal userPrincipal = (SocketUserPrincipal) headerAccessor.getUser();
        if (userPrincipal == null) {
            return null;
        }
        User user = userPrincipal.getUser();
        Channel channel = channelService.findByKey(messageDto.getChannelId());
        Message message = new Message(messageDto.getContent(), user, channel);
//        messageService.addMessage(message);

        return message;
    }

    @MessageMapping("/channel/online")
    @SendTo("/channel/subscribe")
    public void onlineUserSocket(MessageDto messageDto) {
        // find online users
    }
}