package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.UserDto;
import com.chattime.chattime_api.dto.auth.LoginDto;
import com.chattime.chattime_api.dto.auth.RegisterDto;
import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.login.LoginDataResponse;
import com.chattime.chattime_api.dto.response.login.LoginResponse;
import com.chattime.chattime_api.dto.response.register.RegisterDataResponse;
import com.chattime.chattime_api.dto.response.register.RegisterResponse;
import com.chattime.chattime_api.dto.response.user.ProfileDataResponse;
import com.chattime.chattime_api.model.Otp;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.service.OtpService;
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
import java.util.*;

@RestController
public class AuthUserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UtilService utilService;

    @Autowired
    private OtpService otpService;

    @PostMapping("/register")
    public BaseResponse<Object> register(
        @RequestParam("firstname") String firstname,
        @RequestParam("lastname") String lastname,
        @RequestParam("email") String email,
        @RequestParam("phone") String phone,
        @RequestParam("password") String password
    ) {
        RegisterDto userDto = new RegisterDto(firstname.toLowerCase(), firstname, lastname, email, phone, null);
        UUID identifier = UUID.randomUUID();
        String otpCode = otpService.generateOtp(identifier.toString(), Otp.TYPE_REGISTER, 15);
        userDto.setOtp(otpCode);
        userService.sendOTPVerification(userDto);
        return new BaseResponse<>(true, "OTP Code has been send to your email address."
        );
    }

    @PostMapping("/login")
    public BaseResponse<Object> login(@RequestBody LoginDto loginDto) {
        User user = userService.findByEmail(loginDto.getEmail());
        if(user == null) return new BaseResponse<>(false, "Invalid credentials");
        String access_token = userService.verify(loginDto, user);
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
        return new BaseResponse<>(true, loginDataResponse);
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
