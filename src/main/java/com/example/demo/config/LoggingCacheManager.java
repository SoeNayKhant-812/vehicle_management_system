package com.example.demo.config;

import com.example.demo.service.UserService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class LoggingCacheManager implements CacheManager {

    private final CacheManager delegate;
    private final UserService userService;

    public LoggingCacheManager(CacheManager delegate, UserService userService) {
        this.delegate = delegate;
        this.userService = userService;
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = delegate.getCache(name);
        return (cache == null) ? null : new LoggingCache(cache, name, userService);
    }

    @Override
    public java.util.Collection<String> getCacheNames() {
        return delegate.getCacheNames();
    }
}
