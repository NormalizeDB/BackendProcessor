package com.normalizedb.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.impl.JWTParser;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Payload;
import com.normalizedb.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JWTValidator extends BasicAuthenticationFilter {

    @Autowired
    private SecurityConstants constants;

    public JWTValidator(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token = extractToken(request);
        DecodedJWT jwt;
        try {
            jwt = JWT.require(Algorithm.HMAC256(constants.getJwtSecret()))
                    .build()
                    .verify(token);
        } catch (JWTVerificationException ex) {
            throw new AccessDeniedException("Failed to parse Bearer token", ex);
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
        if(headerVal == null || !headerVal.split(" ")[0].equals(constants.getJwtPrefix())) {
            throw new AccessDeniedException("Bearer token missing! Access denied");
        }
        String[] props = headerVal.split(" ");
        assert props.length == 2;
        return props[1];
    }
}
