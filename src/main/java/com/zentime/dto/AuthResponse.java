package com.zentime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Response body for {@code POST /auth/register} and {@code POST /auth/login}. */
@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
}