package com.example.quiz_app_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private boolean correct;
    private boolean selected;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Version
    private int version;
}

