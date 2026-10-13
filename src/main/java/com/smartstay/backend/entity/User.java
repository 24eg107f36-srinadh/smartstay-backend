package com.smartstay.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Email verification
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "email_verification_otp")
    private String emailVerificationOtp;

    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry;

    // Password reset
    @Column(name = "password_reset_otp")
    private String passwordResetOtp;

    @Column(name = "password_reset_otp_expiry")
    private LocalDateTime passwordResetOtpExpiry;

    @Column(name = "password_reset_verified", nullable = false)
    private boolean passwordResetVerified = false;

    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Role {
        OWNER,
        TENANT
    }

    // Default constructor required by JPA
    public User() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Phone
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Role
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // Email verification
    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getEmailVerificationOtp() {
        return emailVerificationOtp;
    }

    public void setEmailVerificationOtp(String emailVerificationOtp) {
        this.emailVerificationOtp = emailVerificationOtp;
    }

    public LocalDateTime getOtpExpiry() {
        return otpExpiry;
    }

    public void setOtpExpiry(LocalDateTime otpExpiry) {
        this.otpExpiry = otpExpiry;
    }

    // Password reset
    public String getPasswordResetOtp() {
        return passwordResetOtp;
    }

    public void setPasswordResetOtp(String passwordResetOtp) {
        this.passwordResetOtp = passwordResetOtp;
    }

    public LocalDateTime getPasswordResetOtpExpiry() {
        return passwordResetOtpExpiry;
    }

    public void setPasswordResetOtpExpiry(LocalDateTime passwordResetOtpExpiry) {
        this.passwordResetOtpExpiry = passwordResetOtpExpiry;
    }

    public boolean isPasswordResetVerified() {
        return passwordResetVerified;
    }

    public void setPasswordResetVerified(boolean passwordResetVerified) {
        this.passwordResetVerified = passwordResetVerified;
    }

    // Timestamps
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt)
     { this.createdAt = createdAt; 

     } 
     public LocalDateTime getUpdatedAt() 
     {
         return updatedAt; 

     } 
     public void setUpdatedAt(LocalDateTime updatedAt) 
     { 
        this.updatedAt = updatedAt; 
     } 
    }