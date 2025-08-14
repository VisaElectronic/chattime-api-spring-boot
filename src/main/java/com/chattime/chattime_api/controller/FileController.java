package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.media.UploadFileResponse;
import com.chattime.chattime_api.service.FileSystemStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileSystemStorageService storage;

    @PostMapping("")
    public BaseResponse<List<UploadFileResponse>> updateProfile(
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam(name="m_type", required = false) Integer m_type
    ) {
        List<UploadFileResponse> fileSources = new ArrayList<>(List.of());
        if (files != null) {
            for (MultipartFile f : files) {
                String path = storage.storeFile(f);
                UploadFileResponse source = new UploadFileResponse(
                        path,
                        f.getOriginalFilename(),
                        f.getContentType(),
                        f.getSize(),
                        m_type
                );
                fileSources.add(source);
            }
        }
        return new BaseResponse<List<UploadFileResponse>>(true, null, fileSources);
    }
}
