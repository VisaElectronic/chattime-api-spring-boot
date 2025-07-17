package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.channel.ChannelSearchData;
import com.chattime.chattime_api.dto.response.channel.GroupDataResponse;
import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.Group;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.model.UserPrincipal;
import com.chattime.chattime_api.service.ChannelService;
import com.chattime.chattime_api.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/group")
public class GroupController {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private GroupService groupService;

    @PostMapping("")
    public BaseResponse<Object> createGroup(
            @RequestPart(name="photo", required = false) List<MultipartFile> photo,
            @RequestPart("groupName") String groupName,
            @RequestParam("channelKeys") List<String> channelKeys
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = ((UserPrincipal) authentication.getPrincipal()).getUser();
        // add current user channel key to body
        channelKeys.add(currentUser.getKey());
        List<Channel> channels = channelService.findAllByKeyIn(channelKeys);
        String key = UUID.randomUUID().toString();
        Group group = groupService.create(
                key,
                groupName,
                photo
        );
        groupService.saveGroupChannels(group, channels);
        return new BaseResponse<>(true, new GroupDataResponse(
            group.getId(),
            group.getName(),
            group.getCustomFirstname(),
            group.getCustomLastname(),
            group.getPhoto(),
            group.getKey(),
            group.getStatus(),
            group.isGroup(),
            null,
            channels
        ));
    }

    @GetMapping("/{key}")
    public BaseResponse<List<ChannelSearchData>> getGroup(
        @PathVariable("key") String key,
        @RequestParam("type") String type
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = ((UserPrincipal) authentication.getPrincipal()).getUser();
        List<ChannelSearchData> channels = List.of();
        if(Objects.equals(type, "MEMBERS")) {
            Group group = groupService.findByKeyAndFetchChannels(key);
            channels = group.getChannels()
                    .stream()
                    .map(ChannelSearchData::from)
                    .toList();
        } else if(Objects.equals(type, "NOT_MEMBERS")) {
            channels = channelService.findAllNotInGroup(key)
                    .stream()
                    .map(ChannelSearchData::from)
                    .toList();
        }
        return new BaseResponse<>(true, channels);
    }

    @PostMapping("/{key}")
    public BaseResponse<Object> updateGroup(
            @PathVariable("key") String groupKey,
            @RequestPart(name="photo", required = false) List<MultipartFile> photo,
            @RequestPart(name="groupName", required = false) String groupName,
            @RequestParam(value = "channelKeys", required = false) List<String> channelKeys
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = ((UserPrincipal) authentication.getPrincipal()).getUser();
        // add current user channel key to body
        channelKeys.add(currentUser.getKey());
        List<Channel> channels = channelService.findAllByKeyIn(channelKeys);
        Group group = groupService.findByKey(groupKey);
        groupService.save(
            groupKey,
            groupName,
            null,
            null,
            photo,
            true
        );
        groupService.saveGroupChannels(group, channels);
        return new BaseResponse<>(true, new GroupDataResponse(
                group.getId(),
                group.getName(),
                group.getCustomFirstname(),
                group.getCustomLastname(),
                group.getPhoto(),
                group.getKey(),
                group.getStatus(),
                group.isGroup(),
                null,
                channels
        ));
    }
}
