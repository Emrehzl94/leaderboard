package com.example.leaderboard.controller;

import com.example.leaderboard.input.ScoreInput;
import com.example.leaderboard.model.User;
import com.example.leaderboard.repository.UserRepository;
import com.example.leaderboard.service.LeaderboardService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.sql.Timestamp;
import java.util.List;

@Log4j2
@RestController
public class LeaderboardController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/leaderboard")
    public ResponseEntity getLeaderboard(@QueryParam("countryCode") String countryCode,
                                         @QueryParam("page") Integer page,
                                         @QueryParam("count") Integer count) {
        log.info("countryCode: " + countryCode + " |page: " + page + " |count: " + count);

        List<User> users;

        page = page == null ? 0 : page;
        count = count == null ? 20 : count;

        try {
            if(countryCode == null){
                users = userRepository.findAll(PageRequest.of(page, count, Sort.by(Sort.Direction.DESC, "points"))).getContent();
            } else {
                users = userRepository.findByCountry(countryCode, PageRequest.of(page, count, Sort.by(Sort.Direction.DESC, "points")));
            }

            LeaderboardService.setRanks(users,  (page * count) + 1);
        } catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occured when leaderboard was fetching.");
        }

        return ResponseEntity.ok().body(users);
    }

    @PostMapping("/score/submit")
    public ResponseEntity scoreSubmit(@RequestBody ScoreInput scoreInput) {
        log.info(scoreInput);

        if(!LeaderboardService.validateScore(scoreInput)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid score.");
        }

        try {
            User user = userRepository.findById(scoreInput.getUser_id()).orElse(null);

            if(user == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User can not be found with id: " + scoreInput.getUser_id());
            }

            user.setPoints(scoreInput.getScore_worth() + user.getPoints());
            userRepository.save(user);

        } catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        return ResponseEntity.ok().body("Score updated.");
    }

    @GetMapping("/user/profile/{user_guid}")
    public ResponseEntity getUserProfile(@PathVariable("user_guid") String userId) {
        log.info("user_id: " + userId);

        User user;
        try {
            user = userRepository.findById(userId).orElse(null);
            if(user == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User cannot be found with id: " + userId);
            }

            long countOfGreaterPoints = userRepository.countByPointsGreaterThan(user.getPoints());
            long countOfEqualPoints = userRepository.countByPointsAndCreationTimeLessThan(user.getPoints(), user.getCreationTime());

            user.setRank(new Long(countOfEqualPoints + countOfGreaterPoints + 1L).intValue());
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occured when user was fetching.");
        }

        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/user/create")
    public ResponseEntity createUser(@RequestBody User user) {
        log.info(user);

        try {
            user.setPoints(0);
            user.setCreationTime(System.currentTimeMillis());
            user = userRepository.save(user);
            Integer rank = new Long(userRepository.count()).intValue();
            user.setRank(rank);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occured when user was creating.");
        }

        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/user/createrandom")
    public ResponseEntity createRandom(@QueryParam("userCount") Integer userCount){
        log.info("userCount: " + userCount);

        try {
            if(userCount == null){
                userCount = 10;
            }

            userRepository.saveAll(LeaderboardService.createUsersRandomly(userCount));
        } catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occured when users were deleting.");
        }

        return ResponseEntity.ok().body("Random users created.");
    }

    @DeleteMapping("/user/batchdelete")
    public ResponseEntity batchDeleteUser(){
        try {
            userRepository.deleteAll();
        } catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occured when users were deleting.");
        }

        return ResponseEntity.ok().body("All users deleted.");
    }
}
