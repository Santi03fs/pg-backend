package com.pg.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class TokenService {

    @Value("${security.jwt.secret:PG_CONSTRUCTORA_SECRET_KEY_2026_SUPER_SECURE_AUTH_TOKEN_KEY_987654321}")
    private String jwtSecret;

    @Value("${security.jwt.expiration-minutes:25}")
    private long expirationMinutes;

    public static class TokenClaims {
        private final Long userId;
        private final String username;
        private final String role;
        private final long issuedAt;
        private final long expiresAt;

        public TokenClaims(Long userId, String username, String role, long issuedAt, long expiresAt) {
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
        }

        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public long getIssuedAt() { return issuedAt; }
        public long getExpiresAt() { return expiresAt; }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }

    public String generateToken(Long userId, String username, String role) {
        long now = System.currentTimeMillis();
        long expiresAt = now + (expirationMinutes * 60 * 1000);

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = String.format(
            "{\"userId\":%d,\"username\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
            userId, escapeJson(username), escapeJson(role), now, expiresAt
        );

        String encodedHeader = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
        String dataToSign = encodedHeader + "." + encodedPayload;

        String signature = sign(dataToSign);
        return dataToSign + "." + signature;
    }

    public TokenClaims validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        String[] parts = token.trim().split("\\.");
        if (parts.length != 3) {
            return null;
        }

        String encodedHeader = parts[0];
        String encodedPayload = parts[1];
        String signature = parts[2];

        // Verify HMAC-SHA256 signature
        String dataToSign = encodedHeader + "." + encodedPayload;
        String expectedSignature = sign(dataToSign);

        if (!MessageDigest.isEqual(signature.getBytes(StandardCharsets.UTF_8), expectedSignature.getBytes(StandardCharsets.UTF_8))) {
            return null; // Invalid signature
        }

        try {
            String payloadJson = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
            
            Long userId = extractLong(payloadJson, "userId");
            String username = extractString(payloadJson, "username");
            String role = extractString(payloadJson, "role");
            long iat = extractLong(payloadJson, "iat");
            long exp = extractLong(payloadJson, "exp");

            if (userId == null || username == null || role == null || exp == 0) {
                return null;
            }

            TokenClaims claims = new TokenClaims(userId, username, role, iat, exp);
            if (claims.isExpired()) {
                return null; // Expired token
            }

            return claims;
        } catch (Exception e) {
            return null;
        }
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error al firmar token", e);
        }
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String extractString(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        start += pattern.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return json.substring(start, end);
    }

    private Long extractLong(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        start += pattern.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
            end++;
        }
        try {
            return Long.parseLong(json.substring(start, end).trim());
        } catch (Exception e) {
            return null;
        }
    }
}
