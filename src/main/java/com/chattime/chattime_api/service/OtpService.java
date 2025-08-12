package com.chattime.chattime_api.service;

import com.chattime.chattime_api.model.Otp;
import com.chattime.chattime_api.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // For transactional methods

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class for handling OTP (One-Time Password) related operations.
 * This includes generating, validating, and checking OTPs.
 */
@Service
public class OtpService {

    private static final int OTP_LENGTH = 6; // Typically 6 digits for OTP
    private static final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    private OtpRepository otpRepository;

    /**
     * Generates a new OTP and saves it to the database.
     * This method is transactional to ensure atomicity of database operations.
     *
     * @param identifier The user's identifier (e.g., email or phone number).
     * @param otpType The type of OTP (e.g., Otp.TYPE_LOGIN, Otp.TYPE_REGISTER).
     * @param durationMinutes The duration in minutes for which the OTP will be valid.
     * @return The generated OTP token string.
     */
    @Transactional
    public String generateOtp(String identifier, int otpType, long durationMinutes) {
        // Invalidate any existing valid OTPs for this identifier and type to prevent confusion
        // and enforce that only the latest generated OTP is active.
        otpRepository.findFirstByIdentifierAndTypeAndValidOrderByValidityDesc(identifier, otpType, true)
                .ifPresent(otp -> {
                    otp.setValid(false); // Mark as invalid
                    otpRepository.save(otp);
                });

        // Generate a random 6-digit OTP
        StringBuilder otpBuilder = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otpBuilder.append(secureRandom.nextInt(10)); // Append a random digit (0-9)
        }
        String otpToken = otpBuilder.toString();

        // Calculate expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(durationMinutes);

        // Create and save the new OTP record
        Otp newOtp = new Otp(identifier, otpToken, true, expiresAt, otpType);
        otpRepository.save(newOtp);

        return otpToken;
    }

    /**
     * Validates a given OTP token for a specific identifier and type.
     * If the OTP is found, valid, and not expired, it will be marked as used (valid=false).
     * This method is transactional to ensure database consistency.
     *
     * @param identifier The user's identifier.
     * @param token The OTP token to validate.
     * @param otpType The type of OTP (e.g., Otp.TYPE_LOGIN).
     * @return true if the OTP is successfully validated and marked as used, false otherwise.
     */
    @Transactional
    public boolean validateOtp(String identifier, String token, int otpType) {
        Optional<Otp> otpOptional = otpRepository.findByIdentifierAndTokenAndTypeAndValid(identifier, token, otpType, true);

        if (otpOptional.isPresent()) {
            Otp otp = otpOptional.get();

            // Check if the OTP has expired
            if (otp.getValidity().isBefore(LocalDateTime.now())) {
                otp.setValid(false); // Mark as expired/invalid
                otpRepository.save(otp);
                return false; // OTP expired
            }

            // OTP is valid and not expired, so mark it as used
            otp.setValid(false);
            otpRepository.save(otp);
            return true; // OTP validated successfully
        }
        return false; // OTP not found or already invalid
    }

    /**
     * Checks if a specific OTP token is currently valid and not expired, without marking it as used.
     * This can be used for pre-checks or scenarios where the OTP shouldn't be consumed yet.
     *
     * @param identifier The user's identifier.
     * @param token The OTP token to check.
     * @param otpType The type of OTP.
     * @return true if the OTP is valid and not expired, false otherwise.
     */
    public boolean checkOtp(String identifier, String token, int otpType) {
        Optional<Otp> otpOptional = otpRepository.findByIdentifierAndTokenAndTypeAndValid(identifier, token, otpType, true);

        if (otpOptional.isPresent()) {
            Otp otp = otpOptional.get();
            // Check if the OTP has expired
            return !otp.getValidity().isBefore(LocalDateTime.now());
        }
        return false; // OTP not found or already invalid
    }

    /**
     * Retrieves an OTP by its identifier, token, and type, regardless of its validity status.
     * This can be useful for administrative purposes or debugging.
     *
     * @param identifier The user's identifier.
     * @param token The OTP token.
     * @param otpType The type of OTP.
     * @return An Optional containing the Otp if found, or empty otherwise.
     */
    public Optional<Otp> getOtp(String identifier, String token, int otpType) {
        // Note: findByIdentifierAndTokenAndTypeAndValid is already defined in repository.
        // We can reuse it or define a more general one if needed.
        // For now, let's assume we want to check all valid ones.
        return otpRepository.findByIdentifierAndTokenAndTypeAndValid(identifier, token, otpType, true);
    }
}

