package com.normalizedb.security.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.normalizedb.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;

@Component
public class JWTGenerator implements AuthenticationSuccessHandler {

    @Autowired
    private SecurityConstants constants;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String token = JWT.create().withSubject((String) authentication.getPrincipal())
                .withIssuedAt(Date.valueOf(LocalDateTime.now().toLocalDate()))
                .sign(Algorithm.HMAC256(constants.getJwtSecret()));
        response.setHeader(HttpHeaders.AUTHORIZATION, String.format("%s %s", constants.getJwtPrefix(), token));
    }
}
