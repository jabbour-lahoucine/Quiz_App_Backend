package com.example.quiz_app_backend.Auth.controllers;

import com.example.quiz_app_backend.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-verification-code")
    public String sendVerificationCode(@RequestParam String email) {
        // Générer un code de vérification aléatoire
        int code = new Random().nextInt(900000) + 100000; // Code à 6 chiffres

        // Envoyer le code par e-mail
        emailService.sendVerificationCode(email, "Votre code de vérification", "Votre code de vérification est : " + code);

        // Retourner un message de succès
        return "Le code de vérification a été envoyé à " + email;
    }
}
