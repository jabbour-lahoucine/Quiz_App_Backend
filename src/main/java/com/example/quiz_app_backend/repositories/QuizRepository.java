package com.example.quiz_app_backend.repositories;

import com.example.quiz_app_backend.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByAccessCode(String accessCode);

    List<Quiz> findByCategory_Id(Long categoryId);
}
