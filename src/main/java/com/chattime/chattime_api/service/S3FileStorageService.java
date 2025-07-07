package com.chattime.chattime_api.service;

import com.chattime.chattime_api.exception.FileStorageException;
import com.chattime.chattime_api.interfaces.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

@Service
public class S3FileStorageService implements FileStorageService {
    private final S3Client s3;
    private final String bucket;

    public S3FileStorageService(
        S3Client s3,
        @Value("${aws.s3.bucket}") String bucket
    ) {
        this.s3 = s3;
        this.bucket = bucket;
    }

    @Override
    public String storeFile(MultipartFile file) {
        String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) ext = original.substring(dot);

        String key = UUID.randomUUID() + ext;

        try {
            if (file.isEmpty()) {
                throw new FileStorageException("Cannot store empty file " + original);
            }
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return key;

        } catch (IOException | S3Exception e) {
            throw new FileStorageException("Failed to store file " + original, e);
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            GetObjectRequest req = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(filename)
                    .build();

            ResponseInputStream<GetObjectResponse> is = s3.getObject(req);
            return new InputStreamResource(is);

        } catch (S3Exception e) {
            throw new FileStorageException("Could not read file: " + filename, e);
        }
    }

    @Override
    public Path getFilePath(String filename) {
        throw new UnsupportedOperationException("Not available when using S3 storage");
    }
}

