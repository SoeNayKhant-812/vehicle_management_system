package com.example.demo.service;

import com.example.demo.repository.CarRepository;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private static final Logger logger = LoggerFactory.getLogger(MetricsService.class);

    private static final String USERS_COUNT_KEY = "vms:metrics:users_count";
    private static final String CARS_COUNT_KEY = "vms:metrics:cars_count";

    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Autowired
    public MetricsService(StringRedisTemplate redisTemplate, UserRepository userRepository, CarRepository carRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    @PostConstruct
    public void initializeCounters() {
        logger.info("Checking metrics counters initialization...");
        if (Boolean.FALSE.equals(redisTemplate.hasKey(USERS_COUNT_KEY))) {
            logger.warn("User count key not found in Redis. Initializing from database...");
            long userCount = userRepository.count();
            redisTemplate.opsForValue().set(USERS_COUNT_KEY, String.valueOf(userCount));
            logger.info("Initialized user count to: {}", userCount);
        }
        if (Boolean.FALSE.equals(redisTemplate.hasKey(CARS_COUNT_KEY))) {
            logger.warn("Car count key not found in Redis. Initializing from database...");
            long carCount = carRepository.count();
            redisTemplate.opsForValue().set(CARS_COUNT_KEY, String.valueOf(carCount));
            logger.info("Initialized car count to: {}", carCount);
        }
        logger.info("Metrics counters are ready.");
    }

    public void incrementUserCount() {
        redisTemplate.opsForValue().increment(USERS_COUNT_KEY);
    }

    public void decrementUserCount() {
        redisTemplate.opsForValue().decrement(USERS_COUNT_KEY);
    }

    public long getUserCount() {
        String count = redisTemplate.opsForValue().get(USERS_COUNT_KEY);
        return count != null ? Long.parseLong(count) : 0;
    }

    public void incrementCarCount() {
        redisTemplate.opsForValue().increment(CARS_COUNT_KEY);
    }

    public void decrementCarCount() {
        redisTemplate.opsForValue().decrement(CARS_COUNT_KEY);
    }

    public long getCarCount() {
        String count = redisTemplate.opsForValue().get(CARS_COUNT_KEY);
        return count != null ? Long.parseLong(count) : 0;
    }
}
