package com.example.quiz_app_backend.repositories;

import com.example.quiz_app_backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Recherche par email (si nécessaire pour des validations futures)
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
