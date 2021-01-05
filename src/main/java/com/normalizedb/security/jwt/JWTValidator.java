package com.normalizedb.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.normalizedb.security.SecurityConstants;
import com.normalizedb.security.handlers.JWTValidatorFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JWTValidator extends OncePerRequestFilter {

    private SecurityConstants constants;
    private JWTValidatorFailureHandler failureHandler;
    private List<RequestMatcher> exclusions;


    @Autowired
    public JWTValidator(SecurityConstants constants,
                        JWTValidatorFailureHandler failureHandler) {
        this.constants = constants;
        this.failureHandler = failureHandler;
        this.exclusions = new ArrayList<>();
    }

    public void registerExcludePattern(String pattern) {
        this.exclusions.add(new AntPathRequestMatcher(pattern));
    }

    private boolean affirmativeRequestExclusion(HttpServletRequest request) {
        if(exclusions.isEmpty()) {
            return false;
        }
        for(RequestMatcher matcher: exclusions) {
            if(matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        //Validate whether authorization checks should occur for a given request.
        //Uses an affirmative voting decision scheme where is one RequestMatcher matches to the following request,
        //the request is opted in to be validated.
        if(affirmativeRequestExclusion(request)) {
            if(logger.isDebugEnabled()) {
                logger.debug(String.format("Skipping authorization.. Method: %s, URI: \"%s\"", request.getMethod(),request.getRequestURI()));
            }
            chain.doFilter(request,response);
            return;
        }

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

        try {
            verifyClaims(jwt.getClaims());
        } catch (IllegalArgumentException ex) {
            handleException("Failed to verify token claims", ex, request, response);
            return;
        }

        //Verify that JWT is not yet expired
        Long expiryMilli = jwt.getClaim(SecurityConstants.Claim.EXPIRES_AT.getKey()).asLong();
        LocalDateTime convertedExpiryDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiryMilli), ZoneId.from(ZoneOffset.UTC));
        if(LocalDateTime.now().isAfter(convertedExpiryDate)) {
            handleException(String.format("Token invalid. Expired on %s", convertedExpiryDate.toString()), null, request, response);
            return;
        }
        //Map raw JWT granted authorities to POJOs
        List<GrantedAuthority> grantedAuthorities = jwt.getClaim(SecurityConstants.Claim.AUTHORITIES.getKey()).asList(String.class)
                                                            .stream()
                                                                .map(SimpleGrantedAuthority::new)
                                                            .collect(Collectors.toList());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                jwt.getSubject(),
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

    private void verifyClaims(Map<String, Claim> claims) {
        for(SecurityConstants.Claim claim : SecurityConstants.Claim.values()) {
            if(!claims.containsKey(claim.getKey())) {
                throw new IllegalArgumentException(String.format("Required token claim is missing: '%s'", claim.getKey()));
            }
        }
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
            throw new ServerErrorException("Attempted Unauthorized Access", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
