package com.chattime.chattime_api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddUserDto {
    private Long userId;

    public AddUserDto() {
    }

    public AddUserDto(Long userId) {
        this.userId = userId;
    }

}
