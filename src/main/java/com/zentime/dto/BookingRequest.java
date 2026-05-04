package com.zentime.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Request body for {@code POST /bookings}.
 */
@Getter
@Setter
public class BookingRequest {

    @NotNull(message = "Service ID must not be null")
    private Long serviceId;

    @NotNull(message = "Start time must not be null")
    @Future(message = "Start time must be in the future")
    private Instant startTime;
}