package com.chattime.chattime_api.service;

import com.chattime.chattime_api.exception.FileStorageException;
import com.chattime.chattime_api.interfaces.FileStorageService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileSystemStorageService implements FileStorageService, InitializingBean {

    private final Path baseLocation;

    public FileSystemStorageService(
            @Value("${app.storage.location}") String storageLocation
    ) {
        this.baseLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
    }

    /** Ensure the directory exists **/
    @Override
    public void afterPropertiesSet() {
        try {
            Files.createDirectories(baseLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not initialize storage location", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) ext = original.substring(dot);
        String generated = UUID.randomUUID().toString() + ext;

        try {
            if (file.isEmpty()) {
                throw new FileStorageException("Cannot store empty file " + original);
            }
            // Prevent path-traversal attacks
            if (generated.contains("..")) {
                throw new FileStorageException("Cannot store file with relative path outside current directory: " + generated);
            }

            Path target = baseLocation.resolve(generated);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return generated;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file " + original, e);
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = baseLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new FileStorageException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new FileStorageException("Malformed URL for file: " + filename, e);
        }
    }

    @Override
    public Path getFilePath(String filename) {
        return baseLocation.resolve(filename).normalize();
    }
}

