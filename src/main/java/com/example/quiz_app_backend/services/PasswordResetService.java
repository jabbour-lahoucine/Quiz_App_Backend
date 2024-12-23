package com.example.quiz_app_backend.services;

import com.example.quiz_app_backend.dto.PasswordResetDTO;
import com.example.quiz_app_backend.entities.User;
import com.example.quiz_app_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class PasswordResetService {

    private final Map<String, String> verificationCodes = new HashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void sendVerificationCode(String email) {
        // Génération d'un code aléatoire
        String code = String.format("%06d", new Random().nextInt(999999));
        verificationCodes.put(email, code);

        // Simuler l'envoi de l'e-mail ou utilisez un vrai service comme précédemment
        System.out.println("Verification code sent to " + email + ": " + code);
    }

    public void resetPassword(PasswordResetDTO passwordResetDTO) {
        String email = passwordResetDTO.getEmail();
        String code = passwordResetDTO.getVerificationCode();
        String newPassword = passwordResetDTO.getNewPassword();

        // Vérifier si le code est valide
        if (!verificationCodes.containsKey(email) || !verificationCodes.get(email).equals(code)) {
            throw new IllegalArgumentException("Invalid verification code.");
        }

        // Rechercher l'utilisateur par e-mail
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        // Hacher le nouveau mot de passe
        String hashedPassword = passwordEncoder.encode(newPassword);

        // Mettre à jour le mot de passe de l'utilisateur
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // Supprimer le code utilisé
        verificationCodes.remove(email);

        System.out.println("Password reset successfully for user: " + email);
    }
}
