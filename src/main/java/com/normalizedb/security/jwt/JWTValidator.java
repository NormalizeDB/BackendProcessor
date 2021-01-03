package com.normalizedb.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.impl.JWTParser;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Payload;
import com.normalizedb.security.SecurityConstants;
import com.normalizedb.security.handlers.JWTValidatorFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTValidator extends OncePerRequestFilter {

    private SecurityConstants constants;
    private JWTValidatorFailureHandler failureHandler;

    @Autowired
    public JWTValidator(SecurityConstants constants,
                        JWTValidatorFailureHandler failureHandler) {
        this.constants = constants;
        this.failureHandler = failureHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        DecodedJWT jwt;
        try{
            String token = extractToken(request);
            if(token == null) {
                handleException("Unable to extract token", null, request, response);
                return;
            }
            jwt = JWT.require(Algorithm.HMAC256(constants.getJwtSecret()))
                    .build()
                    .verify(token);
        } catch (JWTVerificationException ex) {
            handleException("Token verification failure", ex, request, response);
            return;
        }
        JWTParser jwtParser = new JWTParser();
        Payload payload = jwtParser.parsePayload(jwt.getPayload());
        //Verify that JWT is not yet expired
        Long expiryMilli = payload.getClaim(SecurityConstants.Claims.EXPIRES_AT.getValue()).asLong();
        LocalDateTime convertedExpiryDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiryMilli), ZoneId.from(ZoneOffset.UTC));
        if(LocalDateTime.now().isAfter(convertedExpiryDate)) {
            handleException(String.format("Token invalid. Expired on %s", convertedExpiryDate.toString()), null, request, response);
            return;
        }
        //Map raw JWT granted authorities to POJOs
        List<GrantedAuthority> grantedAuthorities = payload.getClaim(SecurityConstants.Claims.AUTHORITIES.getValue()).asList(String.class)
                                                            .stream()
                                                                .map((String rawAuthority) -> new SimpleGrantedAuthority(constants.getAuthorityPrefix() + rawAuthority))
                                                            .collect(Collectors.toList());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                payload.getSubject(),
                null,
                grantedAuthorities
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String headerVal = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String tokenDelim = " ";
        if(headerVal == null || headerVal.split(tokenDelim).length != 2
                || !headerVal.split(tokenDelim)[0].equals(SecurityConstants.TokenType.BEARER.getValue())) {
            return null;
        }
        String[] props = headerVal.split(tokenDelim);
        return props[1];
    }

    private void handleException(String reason, Throwable cause, HttpServletRequest request, HttpServletResponse response) {
        boolean handlerFailed = true;
        if(this.failureHandler != null) {
            this.failureHandler.setFailureReason(reason);
            this.failureHandler.setFailureCause(cause);
            try {
                this.failureHandler.handle(request, response, null);
                handlerFailed = false;
            } catch(Exception ex) { }
        }
        //Default behaviour is to throw an internal server error IF
        // 1. A handler does not succeed
        // 2. A handler is not present
        if(handlerFailed) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Attempted Unauthorized Access");
        }
    }
}
