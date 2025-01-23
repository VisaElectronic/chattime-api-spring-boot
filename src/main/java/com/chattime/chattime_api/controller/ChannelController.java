package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.AddUserDto;
import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.channel.ChannelDataResponse;
import com.chattime.chattime_api.dto.response.channel.GroupDataResponse;
import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.Group;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.model.UserPrincipal;
import com.chattime.chattime_api.service.ChannelService;
import com.chattime.chattime_api.service.GroupService;
import com.chattime.chattime_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {
    @Autowired
    private ChannelService channelService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    // implement create channel
    @PostMapping("")
    public BaseResponse<GroupDataResponse> create(@RequestBody AddUserDto addUserDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = ((UserPrincipal) authentication.getPrincipal()).getUser();
        User user = userService.findById(addUserDto.getUserId());
        Channel channel1 = channelService.create(user.getKey(), null, user);
        channel1.setUser(user);
        Channel channel2 = channelService.create(currentUser.getKey(), null, currentUser);
        String key = UUID.randomUUID().toString();
        Group group = groupService.save(key);
        groupService.saveGroupChannels(group, Set.of(channel1, channel2));
        return new BaseResponse<>(true, new GroupDataResponse(
                group.getId(),
                group.getKey(),
                List.of(channel1, channel2)
        ));
    }

    // implement list of channels
    @GetMapping("")
    public BaseResponse<List<ChannelDataResponse>> list() {
        List<Channel> channels = channelService.findAllActive();

        return new BaseResponse<>(true, ChannelDataResponse.fromList(channels));
    }
}
