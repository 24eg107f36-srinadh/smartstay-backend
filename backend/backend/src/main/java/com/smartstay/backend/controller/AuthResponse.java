package com.smartstay.backend.controller;

public class AuthResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private boolean emailVerified;

    public AuthResponse() {
    }

    public AuthResponse(
            Long id,
            String name,
            String email,
            String phone,
            String role,
            boolean emailVerified) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.emailVerified = emailVerified;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }
}