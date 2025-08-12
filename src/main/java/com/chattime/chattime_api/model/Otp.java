package com.chattime.chattime_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime; // Or java.util.Date if preferred for validity timestamp

@Getter
@Setter
@Entity
@Table(name = "otps")
public class Otp {

    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_REGISTER = 2;
    public static final int TYPE_FORGET_PASSWORD = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key, auto-incremented

    @Column(name = "identifier", nullable = false)
    private String identifier; // User's identifier (e.g., email, phone number)

    @Column(name = "token", nullable = false)
    private String token; // The actual OTP token (e.g., '123456')

    @Column(name = "valid", nullable = false)
    private boolean valid; // Indicates if the OTP is still valid (e.g., not yet used or expired)

    @Column(name = "validity", nullable = false)
    private LocalDateTime validity; // Represents the validity period or expiration timestamp.
    // If it's a duration in seconds, keep as Long.
    // If it's an expiration timestamp, consider LocalDateTime or Instant.
    // For simplicity, assumed to be a duration in seconds or minutes here.

    @Column(name = "type", nullable = false)
    private int type; // Type of OTP, e.g., login or registration

    // Default constructor is required by JPA
    public Otp() {
    }

    // Constructor with all fields (excluding ID as it's auto-generated)
    public Otp(String identifier, String token, boolean valid, LocalDateTime validity, int type) {
        this.identifier = identifier;
        this.token = token;
        this.valid = valid;
        this.validity = validity;
        this.type = type;
    }
}

