package com.normalizedb.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizedb.entities.ApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class AuthorizationFailureHandlerImpl extends JWTValidatorFailureHandler {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        handleFailure(response, accessDeniedException != null ? accessDeniedException : new AccessDeniedException("Access Denied"));
    }

    private void handleFailure(HttpServletResponse response, AccessDeniedException exception) throws IOException {
        ApiError error = new ApiError( HttpStatus.FORBIDDEN.value(),
                                        this.getFailureReason() != null ? this.getFailureReason() : "Attempted Unauthorized Access!",
                                        this.getFailureCause() != null ? this.getFailureCause().getLocalizedMessage()
                                                                        : exception.getLocalizedMessage(),
                                        Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                                        );
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        mapper.writeValue(response.getOutputStream(), error);
        response.flushBuffer();
    }
}
