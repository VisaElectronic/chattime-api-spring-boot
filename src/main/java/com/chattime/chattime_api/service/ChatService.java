package com.chattime.chattime_api.service;

import org.springframework.stereotype.Service;

import com.chattime.chattime_api.model.Message;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {
    private final List<Message> messages = new ArrayList<>();

    public void addMessage(Message message) {
        messages.add(message);
    }

    public List<Message> getAllMessages() {
        return new ArrayList<>(messages);
    }
}
