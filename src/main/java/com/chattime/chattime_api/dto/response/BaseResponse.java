package com.chattime.chattime_api.dto.response;

import jakarta.annotation.Nullable;

public class BaseResponse<T> {
    private Boolean success;
    private T data;

    public BaseResponse(Boolean success, @Nullable T data) {
        this.success = success;
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
