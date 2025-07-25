package com.chattime.chattime_api.dto.response.media;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UploadFileResponse {
    private String uri;
    private String name;
    private String type;
    private Long size;
    private Integer m_type;

    public UploadFileResponse(
        String uri,
        String name,
        String type,
        Long size,
        Integer m_type
    ) {
        this.uri = uri;
        this.name = name;
        this.type = type;
        this.size = size;
        this.m_type = m_type;
    }
}
