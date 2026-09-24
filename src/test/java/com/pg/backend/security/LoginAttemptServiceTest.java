package com.pg.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    private LoginAttemptService attemptService;

    @BeforeEach
    void setUp() {
        attemptService = new LoginAttemptService();
    }

    @Test
    void testNotBlockedInitially() {
        assertFalse(attemptService.isBlocked("192.168.1.1", "admin"));
    }

    @Test
    void testBlockedAfter5FailedAttempts() {
        String ip = "192.168.1.50";
        String user = "victim";

        for (int i = 0; i < 4; i++) {
            attemptService.loginFailed(ip, user);
            assertFalse(attemptService.isBlocked(ip, user), "Should not be blocked at attempt " + (i + 1));
        }

        // 5th attempt
        attemptService.loginFailed(ip, user);
        assertTrue(attemptService.isBlocked(ip, user), "Should be blocked on 5th failure");
        assertTrue(attemptService.getRemainingLockoutMinutes(ip, user) > 0);
    }

    @Test
    void testResetOnSuccess() {
        String ip = "192.168.1.99";
        String user = "user1";

        attemptService.loginFailed(ip, user);
        attemptService.loginFailed(ip, user);
        attemptService.loginSucceeded(ip, user);

        assertFalse(attemptService.isBlocked(ip, user));
    }
}
