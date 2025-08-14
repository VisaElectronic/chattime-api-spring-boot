package com.chattime.chattime_api.service;

import com.chattime.chattime_api.dto.UserDto;
import com.chattime.chattime_api.dto.auth.LoginDto;
import com.chattime.chattime_api.dto.auth.RegisterDto;
import com.chattime.chattime_api.dto.auth.VerifyRegisterDto;
import com.chattime.chattime_api.dto.request.ProfileUpdateDto;
import com.chattime.chattime_api.model.User;
import com.chattime.chattime_api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private FileSystemStorageService storage;

    @Value("${admin.api.url}")
    private String adminApiUrl;

    @Value("${app.request.secret}")
    private String appRequestSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User register(VerifyRegisterDto userDto) {
        User user = new User(
            userDto.getUsername(),
            userDto.getFirstname(),
            userDto.getLastname(),
            userDto.getEmail(),
            userDto.getPhone(),
            encoder.encode(userDto.getPassword())
        );
        return userRepository.save(user);
    }

    public String verify(LoginDto loginDto, User user) {
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
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

    public List<User> findAllAndRemoveCurrentUser(User user) {
        List<User> users = userRepository.findAll();
        users.remove(user);
        return users;
    }

    public User findByKey(String key) {
        return userRepository.findByKey(key);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    public User updateProfile(
            User user,
            ProfileUpdateDto dto,
            List<MultipartFile> files
    ) {
        // 2) update fields
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setBio(dto.getBio());
        user.setDob(dto.getDob());

        // Store each file and collect the relative filenames:
        List<String> stored = new ArrayList<>();
        if (files != null) {
            for (MultipartFile f : files) {
                String filename = storage.storeFile(f);
                stored.add(filename);
            }
        }

        // If you just want the first upload as "avatar":
        String avatarFilename = stored.isEmpty() ? null : stored.getFirst();
        if(avatarFilename != null) user.setAvatar(avatarFilename);

        return userRepository.save(user);
    }

    @Async
    public void sendOTPVerification(RegisterDto data) {
        try {
            // 1. Convert the data object to a JSON string (raw request body)
            String jsonBody = objectMapper.writeValueAsString(data);

            // 2. Calculate the HMAC-SHA256 hash of the JSON body
            String hash = calculateHmacSha256(jsonBody, appRequestSecret);

            // 3. Prepare headers for the outgoing request to Laravel
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Signed-Key", hash); // Set the custom header for Laravel to verify

            // 4. Create the HTTP entity (body + headers)
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            // 5. Send the POST request to the Laravel API
            ResponseEntity<String> response = restTemplate.exchange(
                    adminApiUrl + "/api/v1/send-registration-verify",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            logger.info(response.toString());

        } catch (Exception ex) {
            logger.error(ex.toString());
        }
    }

    private String calculateHmacSha256(String data, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hashedBytes = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // Convert byte array to a hex string for comparison with Laravel's hash_hmac default output
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
