package com.chattime.chattime_api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private String firstname;
    private String username;
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
