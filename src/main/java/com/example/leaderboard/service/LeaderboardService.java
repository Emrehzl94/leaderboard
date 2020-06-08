package com.example.leaderboard.service;

import com.example.leaderboard.input.ScoreInput;
import com.example.leaderboard.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LeaderboardService {

    public static Integer maxPoint = 1000000;

    public static String[] countries = {"tr", "us", "fr", "es", "it", "ru", "de", "gr", "jp", "nl"};

    public static void setRanks(List<User> users, Integer rank){
        for(User user : users){
            user.setRank(rank);
            rank++;
        }
    }

    public static Boolean validateScore(ScoreInput scoreInput){
        if(scoreInput.getScore_worth() < 0){
            return false;
        }

        return true;
    }

    public static List<User> createUsersRandomly(Integer count){
        List<User> users = new ArrayList<>();

        Long currentMs = System.currentTimeMillis();
        Random random = new Random();

        for(int index = 0; index < count; index++){
            User user = new User();
            user.setDisplay_name("display_name-" + index);
            user.setPoints(random.nextInt(maxPoint));
            user.setCountry(countries[random.nextInt(countries.length)]);
            user.setCreationTime(currentMs + new Long(index));

            users.add(user);
        }

        return users;
    }
}
