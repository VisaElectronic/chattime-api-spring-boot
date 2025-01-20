package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.channel.ChannelDataResponse;
import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {
    @Autowired
    private ChannelService channelService;

    // implement create channel
    @PostMapping("")
    public BaseResponse<ChannelDataResponse> create(String key, String name) {
        Channel channel = channelService.create(key, name);
        return new BaseResponse<>(true, new ChannelDataResponse(
                channel.getId(),
                channel.getKey(),
                channel.getName()
        ));
    }

    // implement list of channels
    @GetMapping("")
    public BaseResponse<List<ChannelDataResponse>> list() {
        List<Channel> channels = channelService.findAll();
        return new BaseResponse<>(true, ChannelDataResponse.fromList(channels));
    }
}
