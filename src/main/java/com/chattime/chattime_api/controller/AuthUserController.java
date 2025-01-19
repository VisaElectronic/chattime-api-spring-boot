package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.UserDto;
import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.login.LoginDataResponse;
import com.chattime.chattime_api.dto.response.login.LoginResponse;
import com.chattime.chattime_api.dto.response.register.RegisterDataResponse;
import com.chattime.chattime_api.dto.response.register.RegisterResponse;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@RestController
public class AuthUserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public RegisterResponse register(
        @RequestParam("username") String username,
        @RequestParam("email") String email,
        @RequestParam("password") String password,
        @RequestPart("files") List<MultipartFile> files
    ) {
        UserDto userDto = new UserDto(email, password, files);
        User user = userService.register(userDto);
        if (userDto.getFiles() != null) {
            userDto.getFiles().forEach(file -> {
                try {
                    saveFile(file);
                } catch (IOException e) {
                    throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
                }
            });
        }
        return new RegisterResponse(true, new RegisterDataResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail())
        );
    }

    private void saveFile(MultipartFile file) throws IOException {
        Path resourceDirectory = Paths.get("src", "main", "resources", "uploads");
        if (!Files.exists(resourceDirectory)) {
            Files.createDirectories(resourceDirectory);
        }
        Path filePath = resourceDirectory.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        Files.write(filePath, file.getBytes());
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody UserDto userDto) {
        String access_token = userService.verify(userDto);
        LoginDataResponse loginDataResponse = new LoginDataResponse(access_token);
        return new LoginResponse(true, loginDataResponse);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> logout() {
        return new BaseResponse<Boolean>(true, true);
    }
}
