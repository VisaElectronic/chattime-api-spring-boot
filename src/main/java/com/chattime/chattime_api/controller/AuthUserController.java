package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.auth.LoginDto;
import com.chattime.chattime_api.dto.auth.RegisterDto;
import com.chattime.chattime_api.dto.auth.VerifyRegisterDto;
import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.login.LoginDataResponse;
import com.chattime.chattime_api.dto.response.register.RegisterOTPResponse;
import com.chattime.chattime_api.dto.response.user.ProfileDataResponse;
import com.chattime.chattime_api.model.Otp;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.service.OtpService;
import com.chattime.chattime_api.service.UserService;
import com.chattime.chattime_api.service.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

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
        @RequestParam("phone") String phone
    ) {
        RegisterDto userDto = new RegisterDto(firstname.toLowerCase(), firstname, lastname, email, phone, null);
        UUID identifier = UUID.randomUUID();
        String otpCode = otpService.generateOtp(identifier.toString(), Otp.TYPE_REGISTER, 15, null);
        userDto.setOtp(otpCode);
        userService.sendOTPVerification(userDto);
        return new BaseResponse<>(
                true,
                "OTP Code has been send to your email address.",
                new RegisterOTPResponse(
                        identifier.toString()
                )
        );
    }

    @PostMapping("/register-verify")
    public BaseResponse<Object> verifyRegister(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            @RequestParam("identifier") String identifier,
            @RequestParam("otpCode") String otpCode
    ) {
        VerifyRegisterDto userDto = new VerifyRegisterDto(firstname.toLowerCase(), firstname, lastname, email, phone, password);
        boolean success = otpService.validateOtp(identifier, otpCode, Otp.TYPE_REGISTER);
        if(success) {
            User user = userService.register(userDto);
        } else {
            return new BaseResponse<>(false, "Registered Failed, Invalid OTP.", null);
        }
        return new BaseResponse<>(true, "Registered Successfully.", null);
    }

    @PostMapping("/register-resend")
    public BaseResponse<Object> registerResend(
            @RequestParam("email") String email,
            @RequestParam("identifier") String identifier
    ) {
        UUID newIdentifier = UUID.randomUUID();
        String otpToken = otpService.regenerateOtp(identifier, newIdentifier.toString(), Otp.TYPE_REGISTER);
        if(otpToken != null) {
            RegisterDto userDto = new RegisterDto(null, null, null, email, null, otpToken);
            userService.sendOTPVerification(userDto);
        } else {
            return new BaseResponse<>(false, "Something Went Wrong, Please refresh the page.", null);
        }
        return new BaseResponse<>(
                true,
                "Resent OTP Successfully.",
                new RegisterOTPResponse(
                    newIdentifier.toString()
                )
        );
    }

    @PostMapping("/login")
    public BaseResponse<Object> login(@RequestBody LoginDto loginDto) {
        User user = userService.findByEmail(loginDto.getEmail());
        if(user == null) return new BaseResponse<>(false, "Invalid credentials", null);
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
        return new BaseResponse<>(true, null, loginDataResponse);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> logout() {
        return new BaseResponse<Boolean>(true, null, true);
    }

    @MessageMapping("/channel/logout")
    public BaseResponse<Boolean> socketLogout(
        SimpMessageHeaderAccessor headerAccessor
    ) {
        Map<String, Object> sessionAttrs = headerAccessor.getSessionAttributes();
        if (sessionAttrs != null) {
            sessionAttrs.clear();
        }
        return new BaseResponse<Boolean>(true, null, true);
    }
}
