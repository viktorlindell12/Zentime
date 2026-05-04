package com.zentime.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** Request body for {@code POST /businesses}. */
@Getter
@Setter
public class BusinessRequest {

    @NotBlank(message = "Business name must not be blank")
    private String name;
}