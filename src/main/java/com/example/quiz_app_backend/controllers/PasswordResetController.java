package com.example.quiz_app_backend.controllers;

import com.example.quiz_app_backend.dto.PasswordResetDTO;
import com.example.quiz_app_backend.services.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    // Step 1: Request a password reset (send verification code)
    @PostMapping("/reset-password/request")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        passwordResetService.sendVerificationCode(email);
        return ResponseEntity.ok("A verification code has been sent to your email.");
    }

    // Step 2: Reset password after code verification
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDTO passwordResetDTO) {
        passwordResetService.resetPassword(passwordResetDTO);
        return ResponseEntity.ok("Your password has been successfully reset.");
    }
}
