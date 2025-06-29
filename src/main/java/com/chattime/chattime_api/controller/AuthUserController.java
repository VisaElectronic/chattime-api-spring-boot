package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.UserDto;
import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.login.LoginDataResponse;
import com.chattime.chattime_api.dto.response.login.LoginResponse;
import com.chattime.chattime_api.dto.response.register.RegisterDataResponse;
import com.chattime.chattime_api.dto.response.register.RegisterResponse;
import com.chattime.chattime_api.dto.response.user.ProfileDataResponse;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.service.UserService;
import com.chattime.chattime_api.service.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class AuthUserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UtilService utilService;

    @PostMapping("/register")
    public RegisterResponse register(
        @RequestParam("firstname") String firstname,
        @RequestParam("lastname") String lastname,
        @RequestParam("email") String email,
        @RequestParam("phone") String phone,
        @RequestParam("password") String password
    ) {
        UserDto userDto = new UserDto(firstname.toLowerCase(), firstname, lastname, email, phone, password);
        User user = userService.register(userDto);
        return new RegisterResponse(true, new RegisterDataResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail())
        );
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody UserDto userDto) {
        User user = userService.findByEmail(userDto.getEmail());
        String access_token = userService.verify(userDto, user);
        ProfileDataResponse profile = new ProfileDataResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatar(),
                user.getKey(),
                user.getFirstname(),
                user.getLastname(),
                user.getPhone(),
                user.getDob(),
                user.getBio()
        );
        LoginDataResponse loginDataResponse = new LoginDataResponse(access_token, profile);
        return new LoginResponse(true, loginDataResponse);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> logout() {
        return new BaseResponse<Boolean>(true, true);
    }

    @MessageMapping("/channel/logout")
    public BaseResponse<Boolean> socketLogout(
        SimpMessageHeaderAccessor headerAccessor
    ) {
        Map<String, Object> sessionAttrs = headerAccessor.getSessionAttributes();
        if (sessionAttrs != null) {
            sessionAttrs.clear();
        }
        return new BaseResponse<Boolean>(true, true);
    }
}
