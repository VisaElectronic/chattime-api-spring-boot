package com.chattime.chattime_api.dto.response.register;

import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.login.LoginDataResponse;

public class RegisterResponse extends BaseResponse<RegisterDataResponse> {
    public RegisterResponse(boolean b, RegisterDataResponse registerDataResponse) {
        super(b, registerDataResponse);
    }
}
