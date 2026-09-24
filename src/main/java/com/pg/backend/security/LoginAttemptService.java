package com.pg.backend.security;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = TimeUnit.MINUTES.toMillis(15);

    private static class AttemptInfo {
        int attempts;
        long lastAttemptTime;
        long lockoutUntil;

        AttemptInfo(int attempts, long lastAttemptTime, long lockoutUntil) {
            this.attempts = attempts;
            this.lastAttemptTime = lastAttemptTime;
            this.lockoutUntil = lockoutUntil;
        }
    }

    private final ConcurrentHashMap<String, AttemptInfo> attemptsMap = new ConcurrentHashMap<>();

    private String buildKey(String ip, String username) {
        String cleanIp = (ip == null || ip.isBlank()) ? "unknown" : ip.trim();
        String cleanUser = (username == null || username.isBlank()) ? "anonymous" : username.trim().toLowerCase();
        return cleanIp + "::" + cleanUser;
    }

    public boolean isBlocked(String ip, String username) {
        String key = buildKey(ip, username);
        AttemptInfo info = attemptsMap.get(key);
        if (info == null) {
            return false;
        }

        long now = System.currentTimeMillis();
        if (info.lockoutUntil > now) {
            return true;
        }

        // Lockout expired, reset if needed
        if (info.lockoutUntil > 0 && info.lockoutUntil <= now) {
            attemptsMap.remove(key);
            return false;
        }

        // If attempts are older than lockout duration without getting locked out, reset
        if (now - info.lastAttemptTime > LOCKOUT_DURATION_MS) {
            attemptsMap.remove(key);
            return false;
        }

        return false;
    }

    public void loginFailed(String ip, String username) {
        String key = buildKey(ip, username);
        long now = System.currentTimeMillis();

        attemptsMap.compute(key, (k, existing) -> {
            if (existing == null || (now - existing.lastAttemptTime > LOCKOUT_DURATION_MS)) {
                return new AttemptInfo(1, now, 0);
            }
            int newAttempts = existing.attempts + 1;
            long lockoutUntil = (newAttempts >= MAX_ATTEMPTS) ? (now + LOCKOUT_DURATION_MS) : 0;
            return new AttemptInfo(newAttempts, now, lockoutUntil);
        });
    }

    public void loginSucceeded(String ip, String username) {
        String key = buildKey(ip, username);
        attemptsMap.remove(key);
    }

    public long getRemainingLockoutMinutes(String ip, String username) {
        String key = buildKey(ip, username);
        AttemptInfo info = attemptsMap.get(key);
        if (info == null || info.lockoutUntil <= System.currentTimeMillis()) {
            return 0;
        }
        long diff = info.lockoutUntil - System.currentTimeMillis();
        return Math.max(1, TimeUnit.MILLISECONDS.toMinutes(diff) + 1);
    }
}
