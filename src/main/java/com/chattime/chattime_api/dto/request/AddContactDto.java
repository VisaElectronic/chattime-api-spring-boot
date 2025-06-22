package com.chattime.chattime_api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddContactDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public AddContactDto() {
    }

    public AddContactDto(
            String firstName,
            String lastName,
            String phoneNumber
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }
}
