package com.chattime.chattime_api.service;

import com.chattime.chattime_api.dto.MessageDto;
import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.repository.ChannelRepository;
import com.chattime.chattime_api.repository.MessageRepository;
import com.chattime.chattime_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chattime.chattime_api.model.Message;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    public Message addMessage(MessageDto messageDto) {
        User user = userRepository.findById(messageDto.getUserId()).orElseThrow();
        Channel channel = channelRepository.findById(messageDto.getChannelId()).orElseThrow();
        Message message = new Message(
            messageDto.getContent(),
            user,
            channel
        );
        return messageRepository.save(message);
    }

    public List<Message> getMessagesByChannel(Channel channel) {
        return new ArrayList<>(messageRepository.findByChannelId(channel.getId()));
    }
}
