package com.normalizedb.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizedb.security.SecurityConstants;
import com.normalizedb.security.entities.application.AuthToken;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class JWTGenerator implements AuthenticationSuccessHandler {

    private final SecurityConstants constants;
    private final ObjectMapper mapper;

    public JWTGenerator(SecurityConstants constants,
                        @Qualifier("customMapper") ObjectMapper mapper) {
        this.constants = constants;
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Pair<String,LocalDateTime> tokenPair = generateJWT(authentication);
        AuthToken authToken = new AuthToken(tokenPair.getKey(),
                                                SecurityConstants.TokenType.BEARER.getValue(),
                                                tokenPair.getValue().toString(),
                                                Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                                            );
        response.setContentType(MediaType.APPLICATION_JSON);
        response.setStatus(Response.Status.OK.getStatusCode());
        mapper.writeValue(response.getOutputStream(), authToken);
        response.flushBuffer();
    }

    private Pair<String, LocalDateTime> generateJWT(Authentication authentication) {
        LocalDateTime issueTime = LocalDateTime.now();
        LocalDateTime expiryTime = issueTime.plus(constants.getTokenValidity());
        //Convert Collection to List
        String[] parsedAuthorities = new String[authentication.getAuthorities().size()];
        GrantedAuthority[] providedAuthorities = authentication.getAuthorities().toArray(new GrantedAuthority[0]);
        for(int i = 0; i < parsedAuthorities.length; i++) {
            parsedAuthorities[i] = providedAuthorities[i].getAuthority();
        }
        //Our JWT Token consists of 3 payload structures:
        //1. An issue timestamp, representing when the JWT token was created, passed in as a Long value
        //2. An expiry timestamp, representing the expiration time of the JWT token, passed in as a Long value
        //3. An authorities list, consisting of the role(s) applicable to the given user (ie. ADMIN, USER, etc.)
        String token = JWT.create().withSubject((String) authentication.getPrincipal())
                                .withClaim(SecurityConstants.Claim.ISSUED_AT.getKey(), convertTime(issueTime))
                                .withClaim(SecurityConstants.Claim.EXPIRES_AT.getKey(), convertTime(expiryTime))
                                .withArrayClaim(SecurityConstants.Claim.AUTHORITIES.getKey(), parsedAuthorities)
                                .sign(Algorithm.HMAC256(constants.getJwtSecret()));
        return new Pair<>(token, expiryTime);
    }

    private Long convertTime(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
