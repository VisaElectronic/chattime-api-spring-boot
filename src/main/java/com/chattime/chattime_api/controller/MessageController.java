package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.MessageDto;
import com.chattime.chattime_api.dto.SocketToken;
import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.channel.ChannelDataResponse;
import com.chattime.chattime_api.dto.response.channel.GroupDataResponse;
import com.chattime.chattime_api.dto.response.message.MessageDataResponse;
import com.chattime.chattime_api.dto.response.user.ProfileDataResponse;
import com.chattime.chattime_api.dto.socket.AuthDto;
import com.chattime.chattime_api.dto.socket.ConnectChatDto;
import com.chattime.chattime_api.dto.socket.ConnectOnlineDto;
import com.chattime.chattime_api.model.*;
import com.chattime.chattime_api.service.ChannelService;
import com.chattime.chattime_api.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.chattime.chattime_api.service.MessageService;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/channel/{group_id}/chat")
    @SendTo("/channel/{group_id}/chat")
    public BaseResponse<MessageDataResponse> chatSocket(
        @DestinationVariable String group_id,
        MessageDto messageDto,
        StompHeaderAccessor headerAccessor
    ) {
        User user = messageService.getUserFromSocketConnection(headerAccessor);
        Group group = groupService.findByKeyAndFetchChannels(group_id);
        Message message = messageService.addMessage(new Message(messageDto.getContent(), user, group));

        for (Channel channel : group.getChannels()) {
            if (!Objects.equals(channel.getKey(), user.getKey())) {
                List<Group> groups = groupService.findAllByUserKey(channel.getKey(), channel.getUser());
                messagingTemplate.convertAndSend("/channel/" + channel.getKey() + "/online",
                    new BaseResponse<>(true, GroupDataResponse.fromList(groups))
                );
            }
        }

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

    @MessageMapping("/channel/{user_key}/connect")
    @SendTo("/channel/{user_key}/online")
    public BaseResponse<List<GroupDataResponse>> onlineUserSocket(
            @DestinationVariable String user_key,
            ConnectOnlineDto connectDto,
            StompHeaderAccessor headerAccessor
    ) {
        User user = messageService.getUserFromSocketConnection(headerAccessor);
        channelService.create(user_key, user.getUsername(), user);
        List<Group> groups = groupService.findAllByUserKey(user.getKey(), user);
        return new BaseResponse<>(true, GroupDataResponse.fromList(groups));
    }

    @MessageMapping("/channel/{group_id}/chat/connect")
    @SendTo("/channel/{group_id}/chat/connect")
    public BaseResponse<List<MessageDataResponse>> chatConnectSocket(
            @DestinationVariable String group_id,
            ConnectChatDto connectDto,
            StompHeaderAccessor headerAccessor
    ) {
        User user = messageService.getUserFromSocketConnection(headerAccessor);
        Group group = groupService.findByKey(group_id);
        List<Message> messages = messageService.getMessagesByGroup(group);
        return new BaseResponse<>(true, MessageDataResponse.fromList(messages));
    }
}