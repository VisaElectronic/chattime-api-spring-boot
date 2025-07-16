package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.request.AddContactDto;
import com.chattime.chattime_api.dto.request.AddGroupDto;
import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.channel.GroupDataResponse;
import com.chattime.chattime_api.dto.response.channel.ChannelSearchData;
import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.Group;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.model.UserPrincipal;
import com.chattime.chattime_api.service.ChannelService;
import com.chattime.chattime_api.service.GroupService;
import com.chattime.chattime_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {
    @Autowired
    private ChannelService channelService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // implement create channel
    @PostMapping("")
    public BaseResponse<Object> create(@RequestBody AddContactDto addContactDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = ((UserPrincipal) authentication.getPrincipal()).getUser();
        User user = userService.findByPhone(addContactDto.getPhoneNumber());
        if(user == null) {
            return new BaseResponse<>(false, "The person with this phone number is not registered on App yet.");
        }
        if(Objects.equals(currentUser.getEmail(), user.getEmail())) {
            return new BaseResponse<>(false, "You can't add yourself as a contact.");
        }
        Channel channel1 = channelService.create(user.getKey(), user.getUsername(), user);
        channel1.setUser(user);
        Channel channel2 = channelService.create(currentUser.getKey(), currentUser.getUsername(), currentUser);
        List<Group> existingGroups = groupService.findGroupsWithKeys(channel1.getKey(), channel2.getKey(), false);
        if (!existingGroups.isEmpty()) {
            Group firstGroup = existingGroups.getFirst();
            return new BaseResponse<>(false, "You already has a contact with this phone's owner.");
        }
        String key = UUID.randomUUID().toString();
        Group group = groupService.save(
                key,
                addContactDto.getFirstName() + ' ' + addContactDto.getLastName(),
                addContactDto.getFirstName(),
                addContactDto.getLastName(),
                null,
                false
        );
        groupService.saveGroupChannels(group, List.of(channel1, channel2));
        return new BaseResponse<>(true, new GroupDataResponse(
                group.getId(),
                group.getName(),
                group.getCustomFirstname(),
                group.getCustomLastname(),
                group.getPhoto(),
                group.getKey(),
                group.getStatus(),
                group.isGroup(),
                channel1,
                List.of(channel1, channel2)
        ));
    }

    @GetMapping("/search")
    public BaseResponse<List<ChannelSearchData>> search(
            @RequestParam String search
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = ((UserPrincipal) authentication.getPrincipal()).getUser();
        var groups = groupService.searchGroups(currentUser.getId(), search);
        List<Channel> searchedChannels = ChannelSearchData.fromGroup(groups, currentUser);
        var channels = searchedChannels
                .stream()
                .map(ChannelSearchData::from)
                .toList();
        return new BaseResponse<List<ChannelSearchData>>(true, channels);
    }

    // implement list of channels
    @GetMapping("")
    public BaseResponse<List<GroupDataResponse>> list() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = ((UserPrincipal) authentication.getPrincipal()).getUser();
        Channel channel = channelService.findByKey(currentUser.getKey());
        List<Group> groups = groupService.findAllByUserKey(channel.getKey(), currentUser);

        return new BaseResponse<>(true, GroupDataResponse.fromList(groups, currentUser));
    }
}
