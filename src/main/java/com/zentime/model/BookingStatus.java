package com.zentime.model;

/** Lifecycle states of a {@link Booking}. Cancelled bookings are excluded from overlap checks. */
public enum BookingStatus {
    BOOKED, CANCELLED
}