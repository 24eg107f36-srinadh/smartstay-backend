package com.smartstay.backend.service;

import com.smartstay.backend.entity.User;
import com.smartstay.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class ForgotPasswordService {

    private final UserRepository userRepository;

    public ForgotPasswordService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateResetOtp(User user) {
        SecureRandom random = new SecureRandom();

        String otp = String.format("%06d", random.nextInt(1_000_000));

        user.setPasswordResetOtp(otp);
        user.setPasswordResetOtpExpiry(
                LocalDateTime.now().plusMinutes(5)
        );

        user.setPasswordResetVerified(false);

        userRepository.save(user);

        return otp;
    }

    public boolean verifyResetOtp(User user, String otp) {

        if (user.getPasswordResetOtp() == null ||
            user.getPasswordResetOtpExpiry() == null) {
            return false;
        }

        if (LocalDateTime.now()
                .isAfter(user.getPasswordResetOtpExpiry())) {
            return false;
        }

        if (!user.getPasswordResetOtp().equals(otp)) {
            return false;
        }

        user.setPasswordResetVerified(true);

        userRepository.save(user);

        return true;
    }

    public void clearResetOtp(User user) {

        user.setPasswordResetOtp(null);
        user.setPasswordResetOtpExpiry(null);
        user.setPasswordResetVerified(false);

        userRepository.save(user);
    }
}

