package com.example.quiz_app_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuizDTO {
    private String title;
    private String description;
    private String difficulty;
    private String visibility;
    private String accessCode;
    private int timeLimit;
    private int maxAttempts;
    private String type;
    private Long categoryId;
    private List<QuestionDTO> questions;
}
