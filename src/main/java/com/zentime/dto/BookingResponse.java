package com.zentime.dto;

import com.zentime.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long serviceId;
    private Long customerId;
    private Instant startTime;
    private Instant endTime;
    private BookingStatus status;
    private Instant createdAt;
}