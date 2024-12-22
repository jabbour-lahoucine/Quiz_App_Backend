package com.example.quiz_app_backend.dto;

import lombok.Data;
import java.util.List;
import java.util.Date;

@Data
public class PerformanceReportDTO {
    private String username;
    private List<QuizPerformanceDTO> quizPerformances;

    @Data
    public static class QuizPerformanceDTO {
        private String quizTitle;
        private int score;
        private int timeTaken;
        private Date attemptDate;
        private List<QuestionPerformanceDTO> questionPerformances;
    }

    @Data
    public static class QuestionPerformanceDTO {
        private String questionContent;
        private boolean correct;
    }
}
