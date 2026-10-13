package com.smartstay.backend.service;

import com.smartstay.backend.entity.User;
import com.smartstay.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final UserRepository userRepository;

    public OtpService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateOtp(User user) {

        String otp = String.format(
                "%06d",
                new Random().nextInt(1000000)
        );

        user.setEmailVerificationOtp(otp);
        user.setOtpExpiry(
                LocalDateTime.now().plusMinutes(5)
        );

        userRepository.save(user);

        return otp;
    }

    public boolean verifyOtp(User user, String otp) {

        if (user.getEmailVerificationOtp() == null ||
                user.getOtpExpiry() == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            return false;
        }

        if (!user.getEmailVerificationOtp().equals(otp)) {
            return false;
        }

        user.setEmailVerified(true);
        user.setEmailVerificationOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);

        return true;
    }
}