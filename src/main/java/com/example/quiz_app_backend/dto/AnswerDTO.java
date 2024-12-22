package com.example.quiz_app_backend.dto;

import lombok.Data;

@Data
public class AnswerDTO {
    private Long id;
    private String content;
    private boolean correct;
}