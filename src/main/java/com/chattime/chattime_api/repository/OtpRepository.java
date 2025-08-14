package com.chattime.chattime_api.repository;

import com.chattime.chattime_api.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Otp entity.
 * Provides methods for database interactions with the 'otps' table.
 */
@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    /**
     * Finds an OTP by identifier, token, and type, specifically looking for valid (not used) OTPs.
     * @param identifier The user's identifier (e.g., email or phone number).
     * @param token The OTP token itself.
     * @param type The type of OTP (e.g., TYPE_LOGIN, TYPE_REGISTER).
     * @param valid A boolean indicating if the OTP is currently valid (true) or already used (false).
     * @return An Optional containing the found Otp, or empty if not found.
     */
    Optional<Otp> findByIdentifierAndTokenAndTypeAndValid(String identifier, String token, int type, boolean valid);

    Optional<Otp> findByIdentifierAndTypeAndValid(String identifier, int type, boolean valid);

    /**
     * Finds the latest valid OTP for a given identifier and type.
     * This can be useful when a user requests multiple OTPs, and you only want the most recent active one.
     * @param identifier The user's identifier.
     * @param type The type of OTP.
     * @param valid A boolean indicating if the OTP is currently valid.
     * @return An Optional containing the latest valid Otp, or empty if none found.
     */
    Optional<Otp> findFirstByIdentifierAndTypeAndValidOrderByValidityDesc(String identifier, int type, boolean valid);
}

