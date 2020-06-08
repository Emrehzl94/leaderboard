package com.example.leaderboard.input;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter @Setter @ToString
public class ScoreInput {
    private Integer score_worth;
    private String user_id;
    private Timestamp timestamp;
}
