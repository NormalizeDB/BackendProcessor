package com.normalizedb.security.handlers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.web.access.AccessDeniedHandler;

@Getter
@Setter
public abstract class JWTValidatorFailureHandler implements AccessDeniedHandler {
    private String failureReason;
    private Throwable failureCause;
    protected void clearFailure() {
        this.failureReason = null;
        this.failureCause = null;
    }
}
