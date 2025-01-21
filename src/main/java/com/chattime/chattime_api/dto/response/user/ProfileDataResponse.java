package com.chattime.chattime_api.dto.response.user;

import com.chattime.chattime_api.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ProfileDataResponse {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String key;

    public ProfileDataResponse(
        Long id,
        String username,
        String email,
        String avatar,
        String key
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static List<ProfileDataResponse> fromList(List<User> users) {
        return users.stream().map(user -> new ProfileDataResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getAvatar(),
            user.getKey()
        )).collect(Collectors.toList());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
