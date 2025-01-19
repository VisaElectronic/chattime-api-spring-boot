package com.chattime.chattime_api.socket;

import com.chattime.chattime_api.model.Channel;
import com.chattime.chattime_api.model.User;

public class SocketManager {
    protected User user;
    protected String content;
    protected Channel channel;
    // create constructor
    public SocketManager(User user) {
        this.user = user;
    }
}
