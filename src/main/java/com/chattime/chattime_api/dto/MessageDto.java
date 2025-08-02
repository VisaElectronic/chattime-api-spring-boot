package com.chattime.chattime_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    private String content;
    private Integer type;
    private String files;

    public MessageDto() {
    }

    public MessageDto(
        String content,
        Integer type,
        String files
    ) {
        this.content = content;
        this.type = type;
        this.files = files;
    }
}
