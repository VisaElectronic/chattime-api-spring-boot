package com.chattime.chattime_api.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProfileUpdateDto {
    private String firstname;
    private String lastname;
    private String bio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dob;

    public ProfileUpdateDto(
        String firstname,
        String lastname,
        String bio,
        LocalDateTime dob
    ) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.bio = bio;
        this.dob = dob;
    }
}
