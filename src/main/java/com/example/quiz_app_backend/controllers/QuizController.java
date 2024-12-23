package com.example.quiz_app_backend.controllers;

import com.example.quiz_app_backend.dto.AnswerDTO;
import com.example.quiz_app_backend.dto.LeaderboardEntryDTO;
import com.example.quiz_app_backend.dto.PerformanceReportDTO;
import com.example.quiz_app_backend.dto.QuizDTO;
import com.example.quiz_app_backend.entities.Quiz;
import com.example.quiz_app_backend.entities.User;
import com.example.quiz_app_backend.entities.UserQuizStats;
import com.example.quiz_app_backend.services.QuizService;
import com.example.quiz_app_backend.services.UserService;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    @Autowired
    private QuizService quizService;

    //creation of the quiz
    @PostMapping("/{userId}/create")
    public ResponseEntity<Quiz> createQuiz(@PathVariable Long userId, @RequestBody QuizDTO quizDTO) {
        Quiz quiz = quizService.createQuiz(userId, quizDTO);
        return ResponseEntity.ok(quiz);
    }

    //Update the quiz
    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody Quiz quiz) {
        Quiz updatedQuiz = quizService.updateQuiz(id, quiz);
        return ResponseEntity.ok(updatedQuiz);
    }

    @DeleteMapping("/{id}")
    public void deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Quiz>>findAll() {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(quizzes);
    }

    //participate to a public the quiz
    @GetMapping("participateToPublicQuiz/{id}")
    public ResponseEntity<Quiz> participateToPublicQuiz(@PathVariable Long id) {
        Quiz quiz = quizService.getQuizById(id);
        return ResponseEntity.ok(quiz);
    }

    //participate to a private quiz
    @GetMapping("/participateToPrivateQuiz/{accessCode}")
    public ResponseEntity<Quiz> participateToPrivateQuiz(@PathVariable String accessCode) {
        Quiz quiz = quizService.participateQuizByAccessCode(accessCode);
        return ResponseEntity.ok(quiz);
    }

    //get List ofv quizzes by category
    @GetMapping("/byCategory/{categoryId}")
    public ResponseEntity<List<Quiz>> findByCategory(@PathVariable Long categoryId) {
        List<Quiz> quizzes = quizService.getQuizzesByCategory(categoryId);
        return ResponseEntity.ok(quizzes);
    }

    //score of the quiz
    @PostMapping("/{userId}/{quizId}/submit")
    public ResponseEntity<Integer> submitQuiz(@PathVariable Long userId, @PathVariable Long quizId, @RequestBody List<AnswerDTO> submittedAnswers) {
        int score = quizService.submitQuiz(userId, quizId, submittedAnswers);
        return ResponseEntity.ok(score);
    }

    @PostMapping("/{userId}/{quizId}/start")
    public ResponseEntity<Void> startQuiz(@PathVariable Long userId, @PathVariable Long quizId) {
        quizService.startQuiz(userId, quizId);
        return ResponseEntity.ok().build();
    }

    // performance in one quiz
    @GetMapping("/{quizId}/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDTO>> getLeaderboard(@PathVariable Long quizId) {
        List<LeaderboardEntryDTO> leaderboard = quizService.getLeaderboard(quizId);
        return ResponseEntity.ok(leaderboard);
    }

    //public quizzes for bibliotheque
    @GetMapping("/bibliotheque/publicQuizzes")
    public ResponseEntity<List<Quiz>> getPublicQuizzes() {
        List<Quiz> quizzes = quizService.getPublicQuizzes();
        return ResponseEntity.ok(quizzes);
    }


    //performance report for the  user since the use if the app
    @GetMapping("/{userId}/performance-report")
    public ResponseEntity<PerformanceReportDTO> getPerformanceReport(@PathVariable Long userId) {
        PerformanceReportDTO report = quizService.getPerformanceReport(userId);
        return ResponseEntity.ok(report);
    }


    // Edit profil
    @PutMapping("/{userId}/editProfile")
    public ResponseEntity<User> editUserProfile(
            @PathVariable Long userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String bio
    ) {
        // Créer une instance de UserService
        UserService userService = new UserService();

        // Appeler la méthode non statique
        User updatedUser = userService.editUserProfile(userId, username, email, bio);
        return ResponseEntity.ok(updatedUser);
    }
}
