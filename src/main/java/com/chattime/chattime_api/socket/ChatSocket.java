package com.chattime.chattime_api.socket;

import com.chattime.chattime_api.dto.MessageDto;
import com.chattime.chattime_api.model.Message;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.service.ChannelService;
import com.chattime.chattime_api.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;

public class ChatSocket extends SocketManager {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChannelService channelService;

    public ChatSocket(MessageDto messageDto, User user) {
        super(user);
        this.content = messageDto.getContent();
        this.channel = this.channelService.findByKey(messageDto.getChannelId());
    }

    public void addMessage() {
        Message message = new Message(
            this.content,
            this.user,
            this.channel
        );
        messageService.addMessage(message);
    }
}
