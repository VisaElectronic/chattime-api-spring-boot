package com.chattime.chattime_api.dto;

public class AddUserDto {
    private Long userId;

    public AddUserDto() {
    }

    public AddUserDto(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
