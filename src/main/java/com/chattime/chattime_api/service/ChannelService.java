package com.chattime.chattime_api.service;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.User;
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

    public Channel create(String key, String name, User user) {
        Channel channel = channelRepository.findByKey(key);
        if (channel != null) {
            channel.setName(name);
            channel.setStatus(1); // Assuming you want to set the status to active
        } else {
            channel = new Channel(key, name, user, 1);
        }
        return channelRepository.save(channel);
    }

    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    // list all active channels
    public List<Channel> findAllActive() {
        return channelRepository.findAllByStatus(1);
    }

    public List<Channel> findAllByKeyIn(List<String> keys) {
        return channelRepository.findAllByKeyIn(keys);
    }
}
