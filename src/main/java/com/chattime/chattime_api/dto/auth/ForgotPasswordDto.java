package com.chattime.chattime_api.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ForgotPasswordDto {
    private String email;

    public ForgotPasswordDto(
            String email
    ) {
        this.email = email;
    }
}
