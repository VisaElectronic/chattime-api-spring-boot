package com.chattime.chattime_api.dto.response.user;

import com.chattime.chattime_api.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ProfileDataResponse {
    private Long id;
    private String username;
    private String email;

    public ProfileDataResponse(
        Long id,
        String username,
        String email
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
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
            user.getEmail()
        )).collect(Collectors.toList());
    }
}
