package com.chattime.chattime_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "groups_channels")
public class GroupChannel {

    @EmbeddedId
    private GroupChannelId id;

    @ManyToOne
    @MapsId("groupId")
    private Group group;

    @ManyToOne
    @MapsId("channelId")
    private Channel channel;

    @Column(nullable = true)
    private int role;

    protected GroupChannel() { }

    public GroupChannel(Group group, Channel channel, int role) {
        this.group = group;
        this.channel = channel;
        this.role = role;
        this.id = new GroupChannelId(group.getId(), channel.getId());
    }
}

