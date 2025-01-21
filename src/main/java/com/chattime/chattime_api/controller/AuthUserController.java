package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.UserDto;
import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.login.LoginDataResponse;
import com.chattime.chattime_api.dto.response.login.LoginResponse;
import com.chattime.chattime_api.dto.response.register.RegisterDataResponse;
import com.chattime.chattime_api.dto.response.register.RegisterResponse;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.service.UserService;
import com.chattime.chattime_api.service.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class AuthUserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UtilService utilService;

    @PostMapping("/register")
    public RegisterResponse register(
        @RequestParam("username") String username,
        @RequestParam("email") String email,
        @RequestParam("password") String password,
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
        UserDto userDto = new UserDto(username, email, password, avatarPath);
        User user = userService.register(userDto);
        return new RegisterResponse(true, new RegisterDataResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail())
        );
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
