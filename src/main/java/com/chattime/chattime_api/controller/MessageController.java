package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.chattime.chattime_api.model.Message;
import com.chattime.chattime_api.service.MessageService;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @MessageMapping("/channel/{id}/publish")
    @SendTo("/channel/{id}/subscribe")
    public void sendMessage(@DestinationVariable String id, MessageDto messageDto) {
        // add message
        // ***
        // send new messages to the channel
        // ***
        // retrieve messages from the channel
    }
}