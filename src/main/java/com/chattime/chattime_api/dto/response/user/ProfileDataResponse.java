package com.chattime.chattime_api.dto.response.user;

import com.chattime.chattime_api.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public class ProfileDataResponse {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String key;
    private String firstname;
    private String lastname;
    private String phone;
    private String dob;
    private String bio;

    public ProfileDataResponse(
        Long id,
        String username,
        String email,
        String avatar,
        String key,
        String firstname,
        String lastname,
        String phone,
        LocalDateTime dob,
        String bio
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.key = key;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.dob = dob != null ? dob.toString() : null;
        this.bio = bio;
    }

    public static List<ProfileDataResponse> fromList(List<User> users) {
        return users.stream().map(user -> new ProfileDataResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getAvatar(),
            user.getKey(),
            user.getFirstname(),
            user.getLastname(),
            user.getPhone(),
            user.getDob(),
            user.getBio()
        )).collect(Collectors.toList());
    }

}
