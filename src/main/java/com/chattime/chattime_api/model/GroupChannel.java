package com.chattime.chattime_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "groups_channels")
public class GroupChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    private Channel channel;

    @Column(nullable = true)
    private int role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected GroupChannel() { }

    public GroupChannel(Group group, Channel channel, int role) {
        this.group = group;
        this.channel = channel;
        this.role = role;
    }
}

