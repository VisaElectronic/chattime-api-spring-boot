package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.message.MessageDataResponse;
import com.chattime.chattime_api.model.Group;
import com.chattime.chattime_api.model.Message;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.model.UserPrincipal;
import com.chattime.chattime_api.service.GroupService;
import com.chattime.chattime_api.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageRestController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private GroupService groupService;

    @GetMapping("")
    public BaseResponse<List<MessageDataResponse>> chatConnectSocket(
            @RequestParam String groupId,
            @RequestParam Integer limit,
            @RequestParam Integer offset
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = ((UserPrincipal) authentication.getPrincipal()).getUser();
        Group group = groupService.findByKey(groupId);
        int page   = offset / limit;
        Pageable pg = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        List<Message> messages = messageService.getMessagesByGroup(group, pg);
        return new BaseResponse<>(true, MessageDataResponse.fromList(messages, currentUser));
    }
}
