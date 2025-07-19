package com.chattime.chattime_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
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
            joinColumns = @JoinColumn(name = "group_id", insertable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "channel_id", insertable = false, updatable = false)
    )
    private Set<Channel> channels;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @OneToMany(
            mappedBy = "group",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<GroupChannel> members = new HashSet<>();

    public void addChannel(Channel channel, int role) {
        GroupChannel gc = new GroupChannel(this, channel, role);
        members.add(gc);
    }

    public void removeChannel(Channel channel) {
        members.removeIf(gc -> gc.getChannel().equals(channel));
    }

    // Constructors
    public Group() {
    }

    public Group(
            String key,
            String fullName,
            String customFirstname,
            String customLastname,
            String profile,
            int isGroup,
            int status
    ) {
        this.key = key;
        this.name = fullName;
        this.customFirstname = customFirstname;
        this.customLastname = customLastname;
        this.photo = profile;
        this.isGroup = isGroup;
        this.status = status;
    }

    public boolean isGroup() {
        return isGroup == 1;
    }

    public void setGroup(int group) {
        isGroup = group;
    }
}