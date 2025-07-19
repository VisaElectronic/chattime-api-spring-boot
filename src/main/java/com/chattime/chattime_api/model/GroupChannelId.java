package com.chattime.chattime_api.model;


import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class GroupChannelId implements Serializable {
    private Long groupId;
    private Long channelId;

    public GroupChannelId(Long groupId, Long channelId) {
        this.groupId = groupId;
        this.channelId = channelId;
    }
}

