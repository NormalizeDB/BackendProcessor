package com.normalizedb.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GenericApiSuccess {
    private int status;
    private LocalDateTime timestamp;
}
