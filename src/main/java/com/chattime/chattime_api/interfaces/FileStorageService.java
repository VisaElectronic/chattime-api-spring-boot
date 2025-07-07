package com.chattime.chattime_api.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {
    /**
     * Store the given multipart file and return the generated filename.
     * The returned filename is relative to the base storage location.
     */
    String storeFile(MultipartFile file);

    /**
     * Load a stored file as a Spring Resource (for download/serve).
     */
    Resource loadAsResource(String filename);

    /**
     * Return the absolute Path on disk for a stored filename.
     */
    Path getFilePath(String filename);
}
