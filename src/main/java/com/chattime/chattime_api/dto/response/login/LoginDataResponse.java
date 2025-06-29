package com.chattime.chattime_api.dto.response.login;

import com.chattime.chattime_api.dto.response.user.ProfileDataResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDataResponse {
    private String accessToken;
    private ProfileDataResponse profile;

    public LoginDataResponse() {
    }

    public LoginDataResponse(
        String access_token,
        ProfileDataResponse profile
    ) {
        this.accessToken = access_token;
        this.profile = profile;
    }
}
