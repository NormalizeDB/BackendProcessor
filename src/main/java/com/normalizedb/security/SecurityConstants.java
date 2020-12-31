package com.normalizedb.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

@Component
public class SecurityConstants {
    public enum Claims {
        ISSUED_AT("issuedAt"),
        EXPIRES_AT("expiresAt");
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
    @Value("${com.normalizedb.secret-jwt}")
    private String jwtSecret;
    public String getJwtSecret() { return jwtSecret; }
    public TemporalAmount getTokenValidity() { return tokenValidity; }
}
