package com.example.leaderboard.repository;

import com.example.leaderboard.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByCountry(String country, Pageable pageable);
    long countByPointsGreaterThan(Integer points);
    long countByPointsAndCreationTimeLessThan(Integer points, Long creationTime);
}
