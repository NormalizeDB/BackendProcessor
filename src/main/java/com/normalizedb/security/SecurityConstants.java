package com.normalizedb.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

@Component
public class SecurityConstants {
    public enum Claims {
        ISSUED_AT("issuedAt"),
        EXPIRES_AT("expiresAt"),
        AUTHORITIES("g_authorities");
        private final String val;
        Claims(String val) {
            this.val = val;
        }
        public String getValue() {
            return val;
        }
    }
    public enum TokenType {
        BEARER("Bearer"),
        REFRESH("Refresh");
        private final String val;
        TokenType(String val) {
            this.val = val;
        }
        public String getValue() {
            return val;
        }
    }
    private static final TemporalAmount tokenValidity = Duration.ofMinutes(30);
    private static final String AUTHORITY_PREFIX = "ROLE_";
    @Value("${com.normalizedb.secret-jwt}")
    private String jwtSecret;

    public String getJwtSecret() { return jwtSecret; }
    public TemporalAmount getTokenValidity() { return tokenValidity; }
    public String getAuthorityPrefix() { return AUTHORITY_PREFIX; }
}
