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

    @ManyToOne()
    @JoinColumn(name = "last_message_id", referencedColumnName = "id")
    private Message lastMessage;

    @Column(nullable = true)
    private Integer unread;

    @Column(nullable = true)
    private Integer displayOrder;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected GroupChannel() { }

    public GroupChannel(
            Group group,
            Channel channel,
            Message lastMessage,
            Integer unread,
            Integer order,
            int role
    ) {
        this.group = group;
        this.channel = channel;
        this.role = role;
        this.lastMessage = lastMessage;
        this.unread = unread;
        this.displayOrder = order;
    }
}

