package com.example.quiz_app_backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String bio;

    // Ajout d'un champ pour indiquer si l'utilisateur a vérifié son compte
    private boolean isAccountVerified = false;

    // Date de création de l'utilisateur
    @Column(nullable = false, updatable = false)
    private String createdAt;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> createdQuizzes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserQuizStats> userQuizStats;

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.LocalDateTime.now().toString();
    }
}
