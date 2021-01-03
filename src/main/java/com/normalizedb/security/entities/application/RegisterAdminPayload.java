package com.normalizedb.security.entities.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterAdminPayload {
    private String username;
    private String password;

    @JsonCreator
    public RegisterAdminPayload(@JsonProperty(value = "username", required = true) String username,
                                @JsonProperty(value = "password", required = true) String password) {
        this.username = username;
        this.password = password;
    }
}
