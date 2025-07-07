package com.chattime.chattime_api.controller;

import com.chattime.chattime_api.dto.UserDto;
import com.chattime.chattime_api.dto.request.ProfileUpdateDto;
import com.chattime.chattime_api.dto.response.BaseResponse;
import com.chattime.chattime_api.dto.response.register.RegisterDataResponse;
import com.chattime.chattime_api.dto.response.register.RegisterResponse;
import com.chattime.chattime_api.dto.response.user.ProfileDataResponse;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.model.UserPrincipal;
import com.chattime.chattime_api.service.UserService;
import com.chattime.chattime_api.service.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UtilService utilService;

    // implement list of users with pagination
    @GetMapping("")
    public BaseResponse<List<ProfileDataResponse>> list() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = ((UserPrincipal) authentication.getPrincipal()).getUser();
        List<User> users = userService.findAllAndRemoveCurrentUser(user);
        return new BaseResponse<>(true, ProfileDataResponse.fromList(users));
    }

    @GetMapping("/profile")
    public BaseResponse<ProfileDataResponse> profile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        User user = userService.findByEmail(email);
        return new BaseResponse<>(true, new ProfileDataResponse(
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
        ));
    }

    @PostMapping("/update-profile")
    public void updateProfile(
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
    }

    @PostMapping("/profile")
    public BaseResponse<ProfileDataResponse> updateProfile(
        @RequestPart("avatar") List<MultipartFile> files,
        @RequestParam("firstname") String firstname,
        @RequestParam("lastname") String lastname,
        @RequestParam("bio") String bio,
        @RequestParam("dob") String dob
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = ((UserPrincipal) authentication.getPrincipal()).getUser();
        LocalDate date = LocalDate.parse(dob);
        LocalDateTime dobFormatted = date.atStartOfDay();
        ProfileUpdateDto dto = new ProfileUpdateDto(
                firstname,
                lastname,
                bio,
                dobFormatted
        );
        user = userService.updateProfile(user, dto, files);
        return new BaseResponse<>(true, new ProfileDataResponse(
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
        ));
    }
}
