package com.normalizedb.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizedb.entities.ApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler, AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        handleFailure(response, exception);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        handleFailure(response, authException);
    }

    private void handleFailure(HttpServletResponse response, AuthenticationException exception) throws IOException {
        ApiError error = new ApiError(  HttpStatus.UNAUTHORIZED.value(),
                "Authentication Failed!",
                exception != null ? exception.getLocalizedMessage() : " ",
                Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        mapper.writeValue(response.getOutputStream(), error);
        response.flushBuffer();
    }
}
