package com.chattime.chattime_api.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class UserDto {
    private String username;
    private String email;
    private String password;
    private List<MultipartFile> files;

    public UserDto() {
    }

    public UserDto(String email, String password, List<MultipartFile> files) {
        this.email = email;
        this.password = password;
        if(files != null && !files.isEmpty()) {
            this.files = files;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }
}
