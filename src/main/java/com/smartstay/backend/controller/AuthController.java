
package com.smartstay.backend.controller;

import com.smartstay.backend.entity.User;
import com.smartstay.backend.repository.UserRepository;
import com.smartstay.backend.security.JwtService;
import com.smartstay.backend.service.EmailService;
import com.smartstay.backend.service.ForgotPasswordService;
import com.smartstay.backend.service.OtpService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final EmailService emailService;
    private final ForgotPasswordService forgotPasswordService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            OtpService otpService,
            EmailService emailService,
            ForgotPasswordService forgotPasswordService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.emailService = emailService;

        // Correctly store the injected service
        this.forgotPasswordService = forgotPasswordService;
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        try {
            user.setRole(
                    User.Role.valueOf(
                            request.getRole().toUpperCase()
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Role must be OWNER or TENANT");
        }

        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        AuthResponse response = new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole().name(),
                savedUser.isEmailVerified()
        );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("Invalid email or password");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return ResponseEntity.status(401)
                    .body("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(token);
    }

    // =========================================================
    // SEND EMAIL VERIFICATION OTP
    // =========================================================

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(
        @RequestParam String email) {
        System.out.println(">>> SEND OTP CONTROLLER REACHED");
        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("User not found");
        }

        if (user.isEmailVerified()) {
            return ResponseEntity.badRequest()
                    .body("Email already verified");
        }

        String otp = otpService.generateOtp(user);

        emailService.sendOtpEmail(
                user.getEmail(),
                otp
        );

        return ResponseEntity.ok(
                "OTP sent successfully"
        );
    }

    // =========================================================
    // VERIFY EMAIL OTP
    // =========================================================

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp) {

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("User not found");
        }

        boolean verified =
                otpService.verifyOtp(user, otp);

        if (!verified) {
            return ResponseEntity.badRequest()
                    .body("Invalid or expired OTP");
        }

        return ResponseEntity.ok(
                "Email verified successfully"
        );
    }

    // =========================================================
    // FORGOT PASSWORD - SEND RESET OTP
    // =========================================================

@PostMapping("/forgot-password")
public ResponseEntity<?> forgotPassword(
        @RequestParam String email) {

    User user = userRepository
            .findByEmail(email.trim())
            .orElse(null);

    if (user == null) {
        return ResponseEntity.badRequest()
                .body("No account found with this email");
    }

    try {
        String otp =
                forgotPasswordService.generateResetOtp(user);

        emailService.sendOtpEmail(
                user.getEmail(),
                otp
        );

        return ResponseEntity.ok(
                "Password reset OTP sent successfully"
        );

    } catch (Exception e) {
        System.err.println(
                "Forgot password OTP failed: "
                        + e.getMessage()
        );
        e.printStackTrace();

        return ResponseEntity.internalServerError()
                .body("Unable to send password reset OTP. Check backend logs.");
    }
}

    // =========================================================
    // VERIFY PASSWORD RESET OTP
    // =========================================================

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<?> verifyResetOtp(
            @RequestParam String email,
            @RequestParam String otp) {

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("User not found");
        }

        boolean verified =
                forgotPasswordService.verifyResetOtp(
                        user,
                        otp
                );

        if (!verified) {
            return ResponseEntity.badRequest()
                    .body("Invalid or expired OTP");
        }

        return ResponseEntity.ok(
                "OTP verified successfully"
        );
    }

    // =========================================================
    // RESET PASSWORD
    // =========================================================

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {

        // Check password confirmation
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest()
                    .body("Passwords do not match");
        }

        // Minimum password length
        if (newPassword.length() < 8) {
            return ResponseEntity.badRequest()
                    .body("Password must be at least 8 characters");
        }

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("User not found");
        }

        // OTP must have been verified first
        if (!user.isPasswordResetVerified()) {
            return ResponseEntity.badRequest()
                    .body("Please verify OTP first");
        }

        // Make sure reset OTP has not expired
        if (user.getPasswordResetOtpExpiry() == null ||
                java.time.LocalDateTime.now()
                        .isAfter(user.getPasswordResetOtpExpiry())) {

            return ResponseEntity.badRequest()
                    .body("OTP expired. Please request a new OTP");
        }

        // Encode the new password using BCrypt
        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        // Clear password reset information
        user.setPasswordResetOtp(null);
        user.setPasswordResetOtpExpiry(null);
        user.setPasswordResetVerified(false);

        // Save the changed password to MySQL
        userRepository.save(user);

        return ResponseEntity.ok(
                "Password changed successfully"
        );
    }
    @GetMapping("/test-public")
public ResponseEntity<String> testPublic() {
    return ResponseEntity.ok("Public endpoint works");
}
}

