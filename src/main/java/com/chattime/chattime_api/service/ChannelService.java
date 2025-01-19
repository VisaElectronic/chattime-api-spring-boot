package com.chattime.chattime_api.service;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelService {
    @Autowired
    private ChannelRepository channelRepository;

    public Channel findByKey(String key) {
        return channelRepository.findByKey(key);
    }
}
