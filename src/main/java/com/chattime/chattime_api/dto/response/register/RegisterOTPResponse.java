package com.chattime.chattime_api.dto.response.register;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterOTPResponse {
    private String identifier;

    public RegisterOTPResponse() {
    }

    public RegisterOTPResponse(String identifier) {
        this.identifier = identifier;
    }
}
