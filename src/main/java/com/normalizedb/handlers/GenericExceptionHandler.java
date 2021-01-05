package com.normalizedb.handlers;

import com.normalizedb.entities.ApiError;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;

import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Provider
@Priority(Ordered.HIGHEST_PRECEDENCE)
public class GenericExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "An Internal Server Error Occurred!",
                                        exception.getLocalizedMessage(),
                                        Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }
}
