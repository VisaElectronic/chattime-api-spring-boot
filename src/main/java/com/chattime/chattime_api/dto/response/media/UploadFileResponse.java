package com.chattime.chattime_api.dto.response.media;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UploadFileResponse {
    private String path;

    public UploadFileResponse(
            String path
    ) {
        this.path = path;
    }
}
