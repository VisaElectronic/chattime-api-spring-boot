package com.chattime.chattime_api.dto.response;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseResponse<T> {
    private Boolean success;
    private T data;
    private String message;

    public BaseResponse(Boolean success, @Nullable String message, @Nullable T data) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
}
