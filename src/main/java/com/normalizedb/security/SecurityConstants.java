package com.normalizedb.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.temporal.TemporalAmount;

@Component
public class SecurityConstants {
    @Value("${com.normalizedb.valid-domains}")
    private String[] validDomains;
    private static final TemporalAmount tokenValidity = Duration.ofMinutes(30);
    private static final String AUTHORITY_PREFIX = "ROLE_";

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

    /**
     * Represents configuration arguments passed into the Java VM
     */
    private enum ConfigProperty {
        DATASOURCE_URL("spring.datasource.url"),
        DATASOURCE_USERNAME("spring.datasource.username"),
        DATASOURCE_PASSWORD("spring.datasource.password"),
        JWT_SECRET("com.normalizedb.jwt-secret");

        private final String key;
        ConfigProperty (String key) {
            this.key = key;
            if(!System.getProperties().containsKey(key)) {
                throw new IllegalStateException(String.format("Failed to provide the following property: \"%s\"", key));
            }
        }
        public String getValue() {
            return key;
        }
    }

    public String getJwtSecret() {
        return System.getProperty(ConfigProperty.JWT_SECRET.getValue());
    }

    public String getDataSourceUrl() {
        return System.getProperty(ConfigProperty.DATASOURCE_URL.getValue());
    }

    public String getDataSourceUsername() {
        return System.getProperty(ConfigProperty.DATASOURCE_USERNAME.getValue());
    }

    public String getDataSourcePassword() {
        return System.getProperty(ConfigProperty.DATASOURCE_PASSWORD.getValue());
    }

    public TemporalAmount getTokenValidity() { return tokenValidity; }

    public String getAuthorityPrefix() { return AUTHORITY_PREFIX; }

    public String[] getValidDomains() { return validDomains; }
}
