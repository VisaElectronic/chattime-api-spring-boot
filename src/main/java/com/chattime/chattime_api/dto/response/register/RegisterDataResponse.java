package com.chattime.chattime_api.dto.response.register;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterDataResponse {
    private Long id;
    private String username;
    private String email;

    public RegisterDataResponse() {
    }

    public RegisterDataResponse(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

}
