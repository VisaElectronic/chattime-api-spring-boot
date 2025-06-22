package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.media.UploadFileResponse;
import com.chattime.chattime_api.service.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private UtilService utilService;

    @PostMapping("")
    public BaseResponse<UploadFileResponse> updateProfile(
            @RequestPart("files") List<MultipartFile> files
    ) {
        final List<Path> filePath = new ArrayList<>(List.of());
        if (files != null) {
            files.forEach(file -> {
                try {
                    filePath.add(utilService.saveFile(file));
                } catch (IOException e) {
                    throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
                }
            });
        }
        String[] parts = !filePath.isEmpty() ? filePath.getFirst().toString().split("/") : new String[0];
        String avatarPath = parts.length > 4 ? String.join("/", Arrays.copyOfRange(parts, 4, parts.length)) : null;

        return new BaseResponse<UploadFileResponse>(true, new UploadFileResponse(avatarPath));
    }
}
