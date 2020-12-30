package com.normalizedb.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecurityConstants {
    private static final String JWT_PREFIX = "Bearer";
    @Value("${com.normalizedb.secret-jwt}")
    private String jwtSecret;
    @Value("${com.normalizedb.secret-salt}")
    private String passwordSecretSalt;

    public String getJwtPrefix() {
        return JWT_PREFIX;
    }
    public String getJwtSecret() {
        return jwtSecret;
    }
    public String getPasswordSecretSalt() {
        return passwordSecretSalt;
    }
}
