package com.example.quiz_app_backend.dto;

import com.example.quiz_app_backend.entities.UserQuizStats;
import lombok.Data;

import java.util.Date;

@Data
public class LeaderboardEntryDTO {
    private String username;
    private int score;
    private int timeTaken;
    private Date attemptDate;

    public LeaderboardEntryDTO(UserQuizStats stats) {
        this.username = stats.getUser().getUsername();
        this.score = stats.getScore();
        this.timeTaken = stats.getTimeTaken();
        this.attemptDate = stats.getAttemptDate();
    }
}
