package com.chattime.chattime_api.dto.response.user;

import com.chattime.chattime_api.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
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

    public static List<ProfileDataResponse> fromList(List<User> users) {
        return users.stream().map(user -> new ProfileDataResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getAvatar(),
            user.getKey()
        )).collect(Collectors.toList());
    }

}
