package com.normalizedb.security.entities.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class AuthToken {
    private String token;
    private String type;
    private String expiry;
    private Timestamp timestamp;
}
