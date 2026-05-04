package com.zentime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

/** Response body for business endpoints. */
@Getter
@AllArgsConstructor
public class BusinessResponse {
    private Long id;
    private String name;
    private Long ownerId;
    private Instant createdAt;
}