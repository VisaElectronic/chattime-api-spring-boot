package com.chattime.chattime_api.dto.response.login;

import com.chattime.chattime_api.dto.response.BaseResponse;

public class LoginResponse extends BaseResponse<LoginDataResponse> {
    public LoginResponse(Boolean success, LoginDataResponse data) {
        super(success, data);
    }
}
