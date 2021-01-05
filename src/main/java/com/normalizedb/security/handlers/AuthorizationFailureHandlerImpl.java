package com.normalizedb.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizedb.entities.ApiError;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
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
public class AuthorizationFailureHandlerImpl extends JWTValidatorFailureHandler {

    private final ObjectMapper mapper;

    public AuthorizationFailureHandlerImpl(@Qualifier("customMapper") ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public synchronized void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        handleFailure(response, accessDeniedException != null ? accessDeniedException : new AccessDeniedException("Access Denied"));
        clearFailure();
    }

    private void handleFailure(HttpServletResponse response, AccessDeniedException exception) throws IOException {
        ApiError error = new ApiError( Response.Status.FORBIDDEN.getStatusCode(),
                                        this.getFailureReason() != null ? this.getFailureReason() : "Attempted Unauthorized Access!",
                                        this.getFailureCause() != null ? this.getFailureCause().getLocalizedMessage()
                                                                        : exception.getLocalizedMessage(),
                                        Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                                        );
        response.setContentType(MediaType.APPLICATION_JSON);
        response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
        mapper.writeValue(response.getOutputStream(), error);
        response.flushBuffer();
    }
}
