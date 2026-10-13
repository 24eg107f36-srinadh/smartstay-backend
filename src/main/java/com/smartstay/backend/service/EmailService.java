
package com.smartstay.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("SmartStay - Password Reset OTP");

        message.setText(
                "Hello,\n\n" +
                "We received a request to reset your SmartStay account password.\n\n" +
                "Your password reset OTP is: " + otp + "\n\n" +
                "Enter this OTP in SmartStay to continue resetting your password.\n\n" +
                "If you did not request a password reset, please ignore this email.\n\n" +
                "Regards,\n" +
                "SmartStay Team"
        );

        mailSender.send(message);
    }

    public void sendRegistrationOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("SmartStay - Email Verification OTP");

        message.setText(
                "Hello,\n\n" +
                "Welcome to SmartStay!\n\n" +
                "Your email verification OTP is: " + otp + "\n\n" +
                "Enter this OTP in SmartStay to verify your email address.\n\n" +
                "If you did not request this code, you can ignore this email.\n\n" +
                "Regards,\n" +
                "SmartStay Team"
        );

        mailSender.send(message);
    }
}

