package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.AddUserDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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
    public BaseResponse<GroupDataResponse> create(@RequestBody AddUserDto addUserDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = ((UserPrincipal) authentication.getPrincipal()).getUser();
        User user = userService.findById(addUserDto.getUserId());
        if(Objects.equals(currentUser.getEmail(), user.getEmail())) {
            throw new IllegalArgumentException("You can't add yourself as a contact");
        }
        Channel channel1 = channelService.create(user.getKey(), user.getUsername(), user);
        channel1.setUser(user);
        Channel channel2 = channelService.create(currentUser.getKey(), currentUser.getUsername(), currentUser);
        List<Group> existingGroups = groupService.findGroupsWithKeys(channel1.getKey(), channel2.getKey());
        if (!existingGroups.isEmpty()) {
            Group firstGroup = existingGroups.getFirst();
            return new BaseResponse<>(true, new GroupDataResponse(
                    firstGroup.getId(),
                    firstGroup.getName(),
                    firstGroup.getPhoto(),
                    firstGroup.getKey(),
                    firstGroup.getStatus(),
                    firstGroup.isGroup(),
                    channel1,
                    List.of(channel1, channel2)
            ));
        }
        String key = UUID.randomUUID().toString();
        Group group = groupService.save(key);
        groupService.saveGroupChannels(group, Set.of(channel1, channel2));
        return new BaseResponse<>(true, new GroupDataResponse(
                group.getId(),
                group.getName(),
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
