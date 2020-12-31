package com.normalizedb.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.impl.JWTParser;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Payload;
import com.normalizedb.security.SecurityConstants;
import com.normalizedb.security.handlers.JWTValidatorFailureHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JWTValidator extends BasicAuthenticationFilter {

    private final SecurityConstants constants;
    private JWTValidatorFailureHandler failureHandler;

    public JWTValidator(AuthenticationManager authenticationManager, SecurityConstants constants) {
      super(authenticationManager);
      this.constants = constants;
    }

    public void setFailureHandler(JWTValidatorFailureHandler failureHandler) {
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
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                payload.getSubject(),
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String headerVal = request.getHeader(HttpHeaders.AUTHORIZATION);
        String tokenDelim = " ";
        if(headerVal == null || headerVal.split(tokenDelim).length != 2
                || !headerVal.split(tokenDelim)[0].equals(SecurityConstants.TokenType.BEARER.getValue())) {
            return null;
        }
        String[] props = headerVal.split(" ");
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
