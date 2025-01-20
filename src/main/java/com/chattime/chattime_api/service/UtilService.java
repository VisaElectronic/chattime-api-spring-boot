package com.chattime.chattime_api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class UtilService {
    public Path saveFile(MultipartFile file) throws IOException {
        Path resourceDirectory = Paths.get("src", "main", "resources", "static/uploads");
        if (!Files.exists(resourceDirectory)) {
            Files.createDirectories(resourceDirectory);
        }
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = Objects.requireNonNull(originalFileName).substring(0, originalFileName.lastIndexOf(".")) + "-" + System.currentTimeMillis() + fileExtension;
        Path filePath = resourceDirectory.resolve(Objects.requireNonNull(fileName));
        Files.write(filePath, file.getBytes());
        return filePath;
    }
}
