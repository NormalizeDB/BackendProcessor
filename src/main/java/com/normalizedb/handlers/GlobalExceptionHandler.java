package com.normalizedb.handlers;

import com.normalizedb.entities.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {Exception.class})
    public ApiError handleGeneralException(HttpServletRequest request, Exception ex) {
        //TODO: Once remote logging is setup, log the Bearer Token associated with each exception
        return new ApiError(    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An Internal Server Error Occured!",
                                ex.getLocalizedMessage(),
                                Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                            );
    }
}
