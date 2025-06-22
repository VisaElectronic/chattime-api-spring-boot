package com.chattime.chattime_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "custom_firstname")
    private String customFirstname;
    @Column(name = "custom_lastname")
    private String customLastname;
    @Column(name = "key")
    private String key;
    private String photo;
    @Column(name = "status")
    private int status; // 1 for active, 0 for inactive
    @Column(name = "is_group")
    private int isGroup;

    @ManyToMany
    @JoinTable(
            name = "groups_channels",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id")
    )
    private Set<Channel> channels;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    // Constructors
    public Group() {
    }

    public Group(
            String key,
            String customFirstname,
            String customLastname,
            int status
    ) {
        this.key = key;
        this.customFirstname = customFirstname;
        this.customLastname = customLastname;
        this.status = status;
    }

    public boolean isGroup() {
        return isGroup == 1;
    }

    public void setGroup(int group) {
        isGroup = group;
    }
}