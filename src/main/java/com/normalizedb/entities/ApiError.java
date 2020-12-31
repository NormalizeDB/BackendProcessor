package com.normalizedb.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class ApiError {
    private int status;
    private String summary;
    private String cause;
    private Timestamp timestamp;
}
