package com.example.quiz_app_backend.repositories;

import com.example.quiz_app_backend.entities.Quiz;
import com.example.quiz_app_backend.entities.User;
import com.example.quiz_app_backend.entities.UserQuizStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserQuizStatsRepository extends JpaRepository<UserQuizStats, Long> {

    Optional<UserQuizStats> findByUserAndQuiz(User user, Quiz quiz);
}
