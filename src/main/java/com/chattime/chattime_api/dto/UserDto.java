package com.chattime.chattime_api.dto;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UserDto {
    private String username;
    private String firstname;
    private String lastname;
    private String phone;
    private String email;
    private String password;

    public UserDto(
        String username,
        String firstname,
        String lastname,
        String email,
        String phone,
        String password
    ) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }
}
