package com.chattime.chattime_api.model;

import java.security.Principal;

public class SocketUserPrincipal implements Principal {
    private final User user;

    public SocketUserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return this.user.getEmail();
    }

    public User getUser() {
        return user;
    }
}
