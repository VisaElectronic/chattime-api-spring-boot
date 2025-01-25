package com.chattime.chattime_api.dto;

public class AddContactDto {
    private String userEmail;

    public AddContactDto() {
    }

    public AddContactDto(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
