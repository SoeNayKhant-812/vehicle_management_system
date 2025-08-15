package com.example.demo.config;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

import java.util.Optional;

public class LoggingCache implements Cache {

    private static final Logger log = LoggerFactory.getLogger(LoggingCache.class);

    private final Cache delegate;
    private final String name;
    private final UserService userService;

    public LoggingCache(Cache delegate, String name, UserService userService) {
        this.delegate = delegate;
        this.name = name;
        this.userService = userService;
    }

    private String userContext() {
        Optional<User> currentUser = userService.getCurrentUserOpt();
        return currentUser
                .map(user -> String.format("userId=%s username='%s'", user.getId(), user.getUsername()))
                .orElse("user=ANONYMOUS");
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object getNativeCache() {
        return delegate.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper value = delegate.get(key);
        if (value != null) {
            log.info("[CACHE HIT] cache='{}' key='{}' ({})", name, key, userContext());
        } else {
            log.info("[CACHE MISS] cache='{}' key='{}' ({})", name, key, userContext());
        }
        return value;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T value = delegate.get(key, type);
        if (value != null) {
            log.info("[CACHE HIT] cache='{}' key='{}' ({})", name, key, userContext());
        } else {
            log.info("[CACHE MISS] cache='{}' key='{}' ({})", name, key, userContext());
        }
        return value;
    }

    @Override
    public <T> T get(Object key, java.util.concurrent.Callable<T> valueLoader) {
        return delegate.get(key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        log.info("[CACHE PUT] cache='{}' key='{}' ({})", name, key, userContext());
        delegate.put(key, value);
    }

    @Override
    public void evict(Object key) {
        log.info("[CACHE EVICT] cache='{}' key='{}' ({})", name, key, userContext());
        delegate.evict(key);
    }

    @Override
    public void clear() {
        log.info("[CACHE CLEAR] cache='{}' ({})", name, userContext());
        delegate.clear();
    }
}
