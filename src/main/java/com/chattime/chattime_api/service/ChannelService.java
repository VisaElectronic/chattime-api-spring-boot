package com.chattime.chattime_api.service;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelService {
    @Autowired
    private ChannelRepository channelRepository;

    public Channel findByKey(String key) {
        return channelRepository.findByKey(key);
    }

    public Channel create(String key, String name) {
        Channel channel = new Channel(key, name);
        return channelRepository.save(channel);
    }

    // list all channels
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }
}
