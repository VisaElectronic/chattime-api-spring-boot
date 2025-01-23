package com.chattime.chattime_api.service;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.Group;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.repository.ChannelRepository;
import com.chattime.chattime_api.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ChannelRepository channelRepository;

    public Group findByKey(String key) {
        return groupRepository.findByKey(key);
    }

    public Group save(String key) {
        Group group = groupRepository.findByKey(key);
        if(group != null) {
            group.setStatus(1);
        } else {
            group = new Group(key, 1);
        }
        return groupRepository.save(group);
    }

    public void saveGroupChannel(Group group, Channel channel) {
        Set<Channel> channels = new HashSet<>();
        channels.add(channel);
        group.setChannels(channels);
        groupRepository.save(group);
    }

    public void saveGroupChannels(Group group, Set<Channel> channels) {
        group.setChannels(new HashSet<>(channels));
        groupRepository.save(group);
    }

    public List<Group> findGroupsWithKeys(String key1, String key2) {
        return groupRepository.findGroupsContainingKeys(Arrays.asList(key1, key2), 2);
    }

    public List<Group> findAllByUserKey(String key, User user) {
        List<Group> groups = groupRepository.findByChannelsKey(key);
        for (Group group : groups) {
            if(!group.isGroup()) {
                List<Channel> channels = channelRepository.findByGroupsIdAndNotKey(group.getId(), user.getKey());
                group.setChannels(new HashSet<>(channels));
            }
        }
        return groups;
    }

    public Group findByKeyAndFetchChannels(String key) {
        List<Group> group = groupRepository.findByKeyAndFetchChannels(key);
        if(!group.isEmpty()) {
            return group.getFirst();
        }
        return null;
    }
}
