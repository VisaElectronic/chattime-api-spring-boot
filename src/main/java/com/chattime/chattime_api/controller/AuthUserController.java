package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.UserDto;
import com.chattime.chattime_api.dto.response.login.LoginDataResponse;
import com.chattime.chattime_api.dto.response.login.LoginResponse;
import com.chattime.chattime_api.dto.response.register.RegisterDataResponse;
import com.chattime.chattime_api.dto.response.register.RegisterResponse;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthUserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody UserDto userDto) {
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
}
