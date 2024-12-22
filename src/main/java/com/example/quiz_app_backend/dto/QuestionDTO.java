package com.example.quiz_app_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private String content;
    private String type;
    private List<AnswerDTO> options;
}
