package com.chattime.chattime_api.service;

import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.channel.ChannelData;
import com.chattime.chattime_api.dto.response.channel.ChannelOnlineResponse;
import com.chattime.chattime_api.dto.response.channel.GroupDataResponse;
import com.chattime.chattime_api.model.*;
import com.chattime.chattime_api.repository.ChannelRepository;
import com.chattime.chattime_api.repository.GroupChannelRepository;
import com.chattime.chattime_api.repository.GroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
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

    @Autowired
    private GroupChannelRepository groupChannelRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Async
    public void notifyGroupAsync(
        String type,
        Group group,
        Channel channel,
        Set<Channel> allChannels,
        Integer unread,
        Message lastMessage
    ) {
        if(allChannels.isEmpty()) {
            allChannels = new HashSet<>(channelRepository.findAllByGroupId(group.getId()));
        }
        if(unread == null) {
            unread = this.incrementUnRead(group, channel);
        }
        ChannelOnlineResponse notifyGroup = new ChannelOnlineResponse(
                type,
                this.getGroupData(
                        group,
                        allChannels,
                        channel,
                        unread,
                        lastMessage
                )
        );
        messagingTemplate.convertAndSend("/channel/" + channel.getKey() + "/online",
                new BaseResponse<>(true, notifyGroup)
        );
    }

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
        List<Group> groups = groupRepository.findByChannelKeyOrderByDisplayOrder(userChannelKey);
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

    public void updateGroupLastMessage(Message lastMessage, Group group) {
        group.setLastMessage(lastMessage);
        groupRepository.save(group);
    }

    public Integer getUnReadByChannel(Group group, Channel channel) {
        GroupChannel groupChannel = groupChannelRepository.findByGroupAndChannel(group, channel);
        return groupChannel.getUnread() != null ? groupChannel.getUnread() : 0;
    }

    public List<GroupDataResponse> fromList(
            List<Group> groups,
            Channel channel,
            User currentUser
    ) {
        return groups.stream().map(group -> {
            Set<Channel> channels = group.getChannels();
            List<ChannelData> channelDTOs = List.of();
            ChannelData recipientChannel = null;
            if(!group.isGroup()) {
                recipientChannel = channels.stream()
                        .map(ch -> new ChannelData(
                                ch.getId(),
                                ch.getKey(),
                                ch.getName(),
                                ch.getUser()
                        ))
                        .filter(item -> !item.getKey().equals(currentUser.getKey()))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException(
                                "No recipient channel found for user " + currentUser.getKey()
                        ));
            }
            Message lastMessage = group.getLastMessage();
            Integer unread = this.getUnReadByChannel(group, channel);
            return new GroupDataResponse(
                    group.getId(),
                    group.getName(),
                    group.getCustomFirstname(),
                    group.getCustomLastname(),
                    group.getPhoto(),
                    group.getKey(),
                    group.getStatus(),
                    group.isGroup(),
                    recipientChannel,
                    channelDTOs,
                    unread,
                    lastMessage
            );
        }).toList();
    }

    public List<GroupDataResponse> getGroupData(
            Group group,
            Set<Channel> channels,
            Channel channel,
            Integer unread,
            Message lastMessage
    ) {
        ChannelData recipientChannel = null;
        if(!group.isGroup()) {
            recipientChannel = channels.stream()
                    .map(ch -> new ChannelData(
                            ch.getId(),
                            ch.getKey(),
                            ch.getName(),
                            ch.getUser()
                    ))
                    .filter(item -> !item.getKey().equals(channel.getKey()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(
                            "No recipient channel found for user " + channel.getKey()
                    ));
        }
        return List.of(new GroupDataResponse(
                group.getId(),
                group.getName(),
                group.getCustomFirstname(),
                group.getCustomLastname(),
                group.getPhoto(),
                group.getKey(),
                group.getStatus(),
                group.isGroup(),
                recipientChannel,
                List.of(),
                unread,
                lastMessage
        ));
    }

    @Transactional
    public void reorderGroupChannel(Group group, Channel channel) {
        // 1. Find the group to reorder
        GroupChannel groupToReorder = groupChannelRepository.findByGroupAndChannel(group, channel);

        // 2. Find the current highest display_order in the database
        Integer maxOrder = groupChannelRepository.findMaxDisplayOrder(channel);

        // 3. Set the group's new display_order to be one more than the max.
        // If there are no groups, start with an order of 1.
        int newOrder = (maxOrder != null) ? maxOrder : 1;

        // 4. Update the other groups' orders to fill the gap left by the moved group.
        // This is necessary if you want to maintain a consecutive sequence of numbers.
        int oldOrder = groupToReorder.getDisplayOrder() != null ? groupToReorder.getDisplayOrder() : 0;
        groupChannelRepository.updateDisplayOrderInBatch(
                channel,
                oldOrder + 1,
                newOrder,
                -1
        );

        // 5. Set the new order on the group and save it
        groupToReorder.setDisplayOrder(newOrder);
        groupChannelRepository.save(groupToReorder);
    }

    public Integer incrementUnRead(Group group, Channel channel) {
        GroupChannel groupChannel = groupChannelRepository.findByGroupAndChannel(group, channel);
        int unread = (groupChannel.getUnread() != null ? groupChannel.getUnread() : 0) + 1;
        groupChannel.setUnread(unread);
        groupChannelRepository.save(groupChannel);
        return unread;
    }

    public void resetUnRead(Group group, Channel channel) {
        GroupChannel groupChannel = groupChannelRepository.findByGroupAndChannel(group, channel);
        groupChannel.setUnread(0);
        groupChannelRepository.save(groupChannel);
    }
}
