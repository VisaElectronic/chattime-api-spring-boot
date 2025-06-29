package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.MessageDto;
import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.channel.ChannelOnlineResponse;
import com.chattime.chattime_api.dto.response.channel.GroupDataResponse;
import com.chattime.chattime_api.dto.response.message.MessageDataResponse;
import com.chattime.chattime_api.dto.response.user.ProfileDataResponse;
import com.chattime.chattime_api.dto.socket.ConnectChatDto;
import com.chattime.chattime_api.dto.socket.ConnectOnlineDto;
import com.chattime.chattime_api.model.*;
import com.chattime.chattime_api.service.ChannelService;
import com.chattime.chattime_api.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.chattime.chattime_api.service.MessageService;

import java.util.Collections;
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
                List<Group> groups = groupService.findAllByUserKey(channel.getKey(), user);
                /* Send To Online Channel For Loading The Channel To Left Side-Bar */
                ChannelOnlineResponse notifyGroup = new ChannelOnlineResponse(
                    "NOTIFY_GROUP",
                        GroupDataResponse.from(group, user, List.of())
                );
                messagingTemplate.convertAndSend("/channel/" + channel.getKey() + "/online",
                    new BaseResponse<>(true, notifyGroup)
                );
            }
        }

        return new BaseResponse<>(true, new MessageDataResponse(
                message.getId(),
                message.getContent(),
                group,
                user,
                message.getCreatedAt(),
                user
        ));
    }

    @MessageMapping("/channel/auth/{id}")
    @SendTo("/channel/auth/{id}")
    public Object authUserSocket(
            @DestinationVariable String id,
            StompHeaderAccessor headerAccessor
    ) {
        if(Objects.equals(headerAccessor.getMessageId(), "token_expired")) {
            return new BaseResponse<>(false, "token_expired");
        }
        User user = messageService.getUserFromSocketConnection(headerAccessor);
        return new BaseResponse<>(true, new ProfileDataResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatar(),
                user.getKey(),
                user.getFirstname(),
                user.getLastname(),
                user.getPhone(),
                user.getDob(),
                user.getBio()
        ));
    }

    @MessageMapping("/channel/{user_key}/connect")
    @SendTo("/channel/{user_key}/online")
    public BaseResponse<ChannelOnlineResponse> onlineUserSocket(
            @DestinationVariable String user_key,
            ConnectOnlineDto connectDto,
            StompHeaderAccessor headerAccessor
    ) {
        User user = messageService.getUserFromSocketConnection(headerAccessor);
        channelService.create(user_key, user.getUsername(), user);
        List<Group> groups = groupService.findAllByUserKey(user.getKey(), user);
        return new BaseResponse<>(true, new ChannelOnlineResponse("LIST_GROUPS", GroupDataResponse.fromList(groups, user)));
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
        int limit  = connectDto.getLimit();
        int offset =  connectDto.getOffset();
        int page   = offset / limit;
        Pageable pg = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        List<Message> messages = messageService.getMessagesByGroup(group, pg);
        Collections.reverse(messages);
        return new BaseResponse<>(true, MessageDataResponse.fromList(messages, user));
    }
}