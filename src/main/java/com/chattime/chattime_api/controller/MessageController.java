package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.MessageDto;
import com.chattime.chattime_api.dto.SocketToken;
import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.channel.ChannelDataResponse;
import com.chattime.chattime_api.dto.response.user.ProfileDataResponse;
import com.chattime.chattime_api.dto.socket.AuthDto;
import com.chattime.chattime_api.dto.socket.ConnectOnlineDto;
import com.chattime.chattime_api.model.*;
import com.chattime.chattime_api.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.chattime.chattime_api.service.MessageService;

import java.util.List;
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
        User user = messageService.getUserFromSocketConnection(headerAccessor);
        Channel channel = channelService.findByKey(messageDto.getChannelId());
        Message message = new Message(messageDto.getContent(), user, channel);
//        messageService.addMessage(message);

        return message;
    }

    @MessageMapping("/channel/auth")
    @SendTo("/channel/auth")
    public BaseResponse<ProfileDataResponse> authUserSocket(
            StompHeaderAccessor headerAccessor
    ) {
        User user = messageService.getUserFromSocketConnection(headerAccessor);
        return new BaseResponse<>(true, new ProfileDataResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatar(),
                user.getKey()
        ));
    }

    @MessageMapping("/channel/connect")
    @SendTo("/channel/online")
    public BaseResponse<List<ChannelDataResponse>> onlineUserSocket(
            ConnectOnlineDto connectDto,
            StompHeaderAccessor headerAccessor
    ) {
        User user = messageService.getUserFromSocketConnection(headerAccessor);
        channelService.create(connectDto.getChannelId(), user.getUsername());
        List<Channel> channels = channelService.findAllActive();
        return new BaseResponse<>(true, ChannelDataResponse.fromList(channels));
    }
}