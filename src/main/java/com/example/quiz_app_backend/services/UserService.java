package com.example.quiz_app_backend.services;

import com.example.quiz_app_backend.entities.User;
import com.example.quiz_app_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public  User editUserProfile(Long userId, String username, String email, String bio) {
        // Récupérer l'utilisateur par son ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId));

        // Vérifier l'unicité de l'email et du nom d'utilisateur
        if (email != null && userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet email est déjà utilisé.");
        }

        if (username != null && userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce nom d'utilisateur est déjà utilisé.");
        }

        // Modifier les champs spécifiés
        if (username != null && !username.isEmpty()) user.setUsername(username);
        if (email != null && !email.isEmpty()) user.setEmail(email);
        if (bio != null && !bio.isEmpty()) user.setBio(bio);

        // Enregistrer les modifications
        return userRepository.save(user);
    }
}
