package com.chattime.chattime_api.service;

import com.chattime.chattime_api.model.Group;
import com.chattime.chattime_api.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

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
}
