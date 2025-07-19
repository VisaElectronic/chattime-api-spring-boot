package com.chattime.chattime_api.service;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.Group;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.repository.ChannelRepository;
import com.chattime.chattime_api.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private FileSystemStorageService storage;

    public Group findByKey(String key) {
        return groupRepository.findByKey(key);
    }

    public Group save(
            String key,
            String fullName,
            String customFirstname,
            String customLastname,
            List<MultipartFile> files,
            boolean isGroup
    ) {
        Group group = groupRepository.findByKey(key);

        List<String> stored = new ArrayList<>();
        if (files != null) {
            for (MultipartFile f : files) {
                String filename = storage.storeFile(f);
                stored.add(filename);
            }
        }
        String photoPath = stored.isEmpty() ? null : stored.getFirst();

        if(group != null) {
            group.setStatus(1);
            fullName = fullName == null ? group.getName() : fullName;
            photoPath = photoPath == null ? group.getPhoto() : photoPath;
            group.setName(fullName);
            group.setPhoto(photoPath);
        } else {
            group = new Group(
                key,
                fullName,
                customFirstname,
                customLastname,
                photoPath,
                isGroup ? 1 : 0,
                1
            );
        }
        return groupRepository.save(group);
    }

    public Group create(
            String key,
            String fullName,
            List<MultipartFile> files
    ) {
        // Store each file and collect the relative filenames:
        List<String> stored = new ArrayList<>();
        if (files != null) {
            for (MultipartFile f : files) {
                String filename = storage.storeFile(f);
                stored.add(filename);
            }
        }
        String photoPath = stored.isEmpty() ? null : stored.getFirst();
        Group group = new Group(
                key,
                fullName,
                null,
                null,
                photoPath,
                1,
                1
        );
        return groupRepository.save(group);
    }

    public void saveGroupChannels(
        Group group,
        List<Channel> channels,
        User currentUser
    ) {
        channels.forEach(ch -> {
            if(currentUser != null && Objects.equals(ch.getKey(), currentUser.getKey())) {
                group.addChannel(ch, 2);
            } else {
                group.addChannel(ch, 1);
            }
        });

        groupRepository.save(group);
    }

    public List<Group> findGroupsWithKeys(String key1, String key2, boolean isGroup) {
        return groupRepository.findGroupsContainingKeys(Arrays.asList(key1, key2), isGroup ? 1 : 0);
    }

    public List<Group> findAllByUserKey(String userChannelKey, User user) {
        List<Group> groups = groupRepository.findByChannelsKey(userChannelKey);
        for (Group group : groups) {
            List<Channel> channels = channelRepository.findByGroupsIdAndNotKey(group.getId(), user.getKey());
            group.setChannels(new HashSet<>(channels));
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

    public List<Group> searchGroups(Long userId, String searchText) {
        return groupRepository.findByUserAndSearchText(userId, searchText.trim());
    }

    public Group findByKeyAndFetchMembers(String key) {
        List<Group> group = groupRepository.findByKeyAndFetchMembers(key);
        if(!group.isEmpty()) {
            return group.getFirst();
        }
        return null;
    }

    public void removeChannel(Group group, Channel channel) {
        group.removeChannel(channel);
        groupRepository.save(group);
    }
}
