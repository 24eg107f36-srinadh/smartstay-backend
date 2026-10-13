package com.smartstay.backend.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class PasswordTestController {

    private final PasswordEncoder passwordEncoder;

    public PasswordTestController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/password")
    public String testPassword(@RequestParam String password,
                               @RequestParam String hash) {
        return passwordEncoder.matches(password, hash)
                ? "PASSWORD_MATCH"
                : "PASSWORD_DOES_NOT_MATCH";
    }
}