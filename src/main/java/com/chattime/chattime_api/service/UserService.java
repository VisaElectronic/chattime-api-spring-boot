package com.chattime.chattime_api.service;

import com.chattime.chattime_api.dto.UserDto;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User register(UserDto userDto) {
        User user = new User(
            userDto.getUsername(),
            userDto.getEmail(),
            encoder.encode(userDto.getPassword()),
            userDto.getAvatar()
        );
        return userRepository.save(user);
    }

    public String verify(UserDto userDto) {
        User user = userRepository.findByEmail(userDto.getEmail());
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));
        if(authentication.isAuthenticated()) {
            return jwtService.generateToken(user);
        }
        throw new RuntimeException("Invalid credentials");
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
