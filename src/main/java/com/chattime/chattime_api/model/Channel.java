package com.chattime.chattime_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "channels")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "key")
    private String key;
    private int status; // 1 for active, 0 for inactive

    @OneToOne
    @JoinColumn(name = "key", referencedColumnName = "key", insertable=false, updatable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private User createdBy;

    @ManyToMany
    @JoinTable(
            name = "groups_channels",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @JsonIgnore
    private Set<Group> groups;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @OneToMany(
            mappedBy = "channel",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<GroupChannel> members = new HashSet<>();

    // Constructors
    public Channel() {
    }

    public Channel(String key, String name, User createdBy, int status) {
        this.key = key;
        this.name = name;
        this.status = status;
        this.createdBy = createdBy;
    }

    public User getUser() {
        user.setPassword(null);
        return user;
    }
}