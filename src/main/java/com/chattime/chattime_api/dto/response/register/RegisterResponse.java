package com.chattime.chattime_api.dto.response.register;

import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.login.LoginDataResponse;
import jakarta.annotation.Nullable;

public class RegisterResponse extends BaseResponse<RegisterDataResponse> {
    public RegisterResponse(boolean b, @Nullable RegisterDataResponse registerDataResponse) {
        super(b, registerDataResponse);
    }
}
