package com.jerry.workoutapp.service;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtBlacklistService {

    // token -> expiration date
    private final Map<String, Date> blacklist = new ConcurrentHashMap<>();

    public void blacklistToken(String token, Date expirationDate) {
        blacklist.put(token, expirationDate);
    }

    public boolean isTokenBlacklisted(String token) {
        Date exp = blacklist.get(token);
        if (exp == null) return false;

        // Remove old tokens
        if (exp.before(new Date())) {
            blacklist.remove(token);
            return false;
        }

        return true;
    }
}
