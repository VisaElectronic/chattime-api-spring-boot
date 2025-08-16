package com.chattime.chattime_api.dto.auth.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ResetPasswordDto {
    private String email;
    private String token;
    private String password;
    private String password_confirmation;

    public ResetPasswordDto(
            String email,
            String token,
            String password,
            String confirmPassword
    ) {
        this.email = email;
        this.token = token;
        this.password = password;
        this.password_confirmation = confirmPassword;
    }
}