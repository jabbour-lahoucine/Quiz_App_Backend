package com.example.quiz_app_backend.controllers;

import com.example.quiz_app_backend.entities.Quiz;
import com.example.quiz_app_backend.services.QuizService;
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
    @PostMapping("/creat/{creatorId}")
    public ResponseEntity<String> createQuiz(@RequestBody Quiz quiz, @PathVariable Long creatorId) {
        String createdQuiz = quizService.createQuiz(quiz, creatorId);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

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

    @GetMapping("participateToPublicQuiz/{id}")
    public ResponseEntity<Quiz> participateToPublicQuiz(@PathVariable Long id) {
        Quiz quiz = quizService.getQuizById(id);
        return ResponseEntity.ok(quiz);
    }

    @GetMapping("/participateToPrivateQuiz/{accessCode}")
    public ResponseEntity<Quiz> participateToPrivateQuiz(@PathVariable String accessCode) {
        Quiz quiz = quizService.participateQuizByAccessCode(accessCode);
        return ResponseEntity.ok(quiz);
    }

    @GetMapping("/byCategory/{categoryId}")
    public ResponseEntity<List<Quiz>> findByCategory(@PathVariable Long categoryId) {
        List<Quiz> quizzes = quizService.getQuizzesByCategory(categoryId);
        return ResponseEntity.ok(quizzes);
    }

}
