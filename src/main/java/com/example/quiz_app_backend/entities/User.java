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
    private String username;
    private String email;
    private String password;
    private String bio;

    @OneToMany(mappedBy = "creator")
    private List<Quiz> createdQuizzes;

    @OneToMany(mappedBy = "user")
    private List<UserQuizStats> userQuizStats;
}

