package com.chattime.chattime_api.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.chattime.chattime_api.model.Message;
import com.chattime.chattime_api.service.ChatService;

@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/channel/{id}/publish")
    @SendTo("/channel/{id}/subscribe")
    public Message sendMessage(@DestinationVariable String id, Message message) {
        chatService.addMessage(message);
        return message;
    }
}