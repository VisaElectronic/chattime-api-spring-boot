package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.MessageDto;
import com.chattime.chattime_api.dto.SocketToken;
import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.channel.ChannelDataResponse;
import com.chattime.chattime_api.dto.response.message.MessageDataResponse;
import com.chattime.chattime_api.dto.response.user.ProfileDataResponse;
import com.chattime.chattime_api.dto.socket.AuthDto;
import com.chattime.chattime_api.dto.socket.ConnectOnlineDto;
import com.chattime.chattime_api.model.*;
import com.chattime.chattime_api.service.ChannelService;
import com.chattime.chattime_api.service.GroupService;
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

    @Autowired
    private GroupService groupService;

    @MessageMapping("/channel/{group_id}/chat")
    @SendTo("/channel/{group_id}/chat")
    public BaseResponse<MessageDataResponse> chatSocket(
        @DestinationVariable String group_id,
        MessageDto messageDto,
        StompHeaderAccessor headerAccessor
    ) {
        User user = messageService.getUserFromSocketConnection(headerAccessor);
        Group group = groupService.findByKey(group_id);
        if(group == null) {
            group = groupService.save(group_id);
        }
        Message message = messageService.addMessage(new Message(messageDto.getContent(), user, group));

        return new BaseResponse<>(true, new MessageDataResponse(
                message.getId(),
                message.getContent(),
                group,
                user,
                message.getCreatedAt()
        ));
    }

    @MessageMapping("/channel/auth/{id}")
    @SendTo("/channel/auth/{id}")
    public BaseResponse<ProfileDataResponse> authUserSocket(
            @DestinationVariable String id,
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

    @MessageMapping("/channel/{group_id}/chat/connect")
    @SendTo("/channel/{group_id}/chat/connect")
    public BaseResponse<List<MessageDataResponse>> chatConnectSocket(
            @DestinationVariable String group_id,
            StompHeaderAccessor headerAccessor
    ) {
        User user = messageService.getUserFromSocketConnection(headerAccessor);
        Group group = groupService.save(group_id);
        List<Message> messages = messageService.getMessagesByGroup(group);
        return new BaseResponse<>(true, MessageDataResponse.fromList(messages));
    }
}