package com.chattime.chattime_api.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {
    private String firstname;
    private String username;
    private String lastname;
    private String phone;
    private String email;
    private String otp;

    public RegisterDto(
            String username,
            String firstname,
            String lastname,
            String email,
            String phone,
            String otp
    ) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.otp = otp;
    }
}
