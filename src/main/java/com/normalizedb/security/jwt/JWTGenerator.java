package com.normalizedb.security.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizedb.security.SecurityConstants;
import com.normalizedb.security.entities.AuthToken;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class JWTGenerator implements AuthenticationSuccessHandler {

    @Autowired
    private SecurityConstants constants;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Pair<String,LocalDateTime> tokenPair = generateJWT(authentication);
        AuthToken authToken = new AuthToken(tokenPair.getKey(),
                                                SecurityConstants.TokenType.BEARER.getValue(),
                                                tokenPair.getValue().toString(),
                                                Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                                            );
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        mapper.writeValue(response.getOutputStream(), authToken);
        response.flushBuffer();
    }

    private Pair<String, LocalDateTime> generateJWT(Authentication authentication) {
        LocalDateTime issueTime = LocalDateTime.now();
        LocalDateTime expiryTime = issueTime.plus(constants.getTokenValidity());
        String token = JWT.create().withSubject((String) authentication.getPrincipal())
                                .withClaim(SecurityConstants.Claims.ISSUED_AT.getValue(), convertTime(issueTime))
                                .withClaim(SecurityConstants.Claims.EXPIRES_AT.getValue(), convertTime(expiryTime))
                                .sign(Algorithm.HMAC256(constants.getJwtSecret()));
        return new Pair<>(token, expiryTime);
    }

    private Long convertTime(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
