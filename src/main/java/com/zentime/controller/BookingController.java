package com.zentime.controller;

import com.zentime.dto.BookingRequest;
import com.zentime.dto.BookingResponse;
import com.zentime.model.User;
import com.zentime.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles booking lifecycle: create, list, and cancel.
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Creates a booking for the authenticated customer.
     *
     * @param request     service ID and desired start time
     * @param currentUser the authenticated customer
     * @return 201 with the created booking
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponse> create(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.create(request, currentUser));
    }

    /**
     * Returns all bookings across every service belonging to the admin's business.
     *
     * @param currentUser the authenticated admin
     * @return 200 with the booking list
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getAllForBusiness(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(bookingService.getAllForBusiness(currentUser));
    }

    /**
     * Cancels a booking. Customers may only cancel their own; admins may cancel
     * any booking within their business.
     *
     * @param id          booking ID to cancel
     * @param currentUser the authenticated user
     * @return 200 with the updated booking
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(bookingService.cancel(id, currentUser));
    }
}