package com.pg.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "jwtSecret", "TEST_SECRET_KEY_FOR_UNIT_TESTING_1234567890");
        ReflectionTestUtils.setField(tokenService, "expirationMinutes", 25L);
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = tokenService.generateToken(1L, "admin", "ADMIN");
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);

        TokenService.TokenClaims claims = tokenService.validateToken(token);
        assertNotNull(claims);
        assertEquals(1L, claims.getUserId());
        assertEquals("admin", claims.getUsername());
        assertEquals("ADMIN", claims.getRole());
        assertFalse(claims.isExpired());
    }

    @Test
    void testTamperedTokenRejected() {
        String token = tokenService.generateToken(2L, "empleado", "USER");
        // Tamper with the payload
        String[] parts = token.split("\\.");
        String tamperedToken = parts[0] + "." + parts[1] + "tampered." + parts[2];

        TokenService.TokenClaims claims = tokenService.validateToken(tamperedToken);
        assertNull(claims);
    }

    @Test
    void testExpiredTokenRejected() {
        // Set expiration to negative (already expired)
        ReflectionTestUtils.setField(tokenService, "expirationMinutes", -5L);
        String token = tokenService.generateToken(1L, "admin", "ADMIN");

        TokenService.TokenClaims claims = tokenService.validateToken(token);
        assertNull(claims);
    }
}
