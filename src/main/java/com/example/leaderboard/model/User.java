package com.example.leaderboard.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Getter @Setter @ToString
public class User {
    @Id
    private String id;
    private String display_name;
    private Integer points;
    private String country;
    private Integer rank;
    private Long creationTime;
}
