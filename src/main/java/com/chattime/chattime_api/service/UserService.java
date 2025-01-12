package com.chattime.chattime_api.service;

import com.chattime.chattime_api.dto.UserDto;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User register(UserDto userDto) {
        User user = new User(
            userDto.getEmail(),
            encoder.encode(userDto.getPassword())
        );
        return userRepository.save(user);
    }
}
