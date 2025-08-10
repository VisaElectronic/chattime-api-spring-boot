package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.channel.ChannelOnlineResponse;
import com.chattime.chattime_api.dto.response.message.MessageDataResponse;
import com.chattime.chattime_api.model.*;
import com.chattime.chattime_api.service.ChannelService;
import com.chattime.chattime_api.service.GroupService;
import com.chattime.chattime_api.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/messages")
public class MessageRestController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ChannelService channelService;

    @GetMapping("")
    public BaseResponse<List<MessageDataResponse>> chatConnectSocket(
            @RequestParam String groupId,
            @RequestParam Integer limit,
            @RequestParam Integer offset
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = ((UserPrincipal) authentication.getPrincipal()).getUser();
        Channel channel = channelService.findByKey(currentUser.getKey());
        Group group = groupService.findByKey(groupId);
        int page   = offset / limit;
        Pageable pg = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        List<Message> messages = messageService.getMessagesByGroup(group, pg);
        groupService.resetUnRead(group, channel);
        /* Send To Online Channel For Loading The Channel To Left Side-Bar */
        groupService.notifyGroupAsync(
                "UPDATE_GROUP",
                group,
                channel,
                Set.of(),
                0,
                null
        );
        return new BaseResponse<>(true, MessageDataResponse.fromList(messages, currentUser));
    }
}
