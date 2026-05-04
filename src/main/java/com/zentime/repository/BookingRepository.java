package com.zentime.repository;

import com.zentime.model.Booking;
import com.zentime.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Returns {@code true} if a BOOKED slot for the given service overlaps [newStartTime, newEndTime).
     * Overlap condition: existing.startTime &lt; newEndTime AND existing.endTime &gt; newStartTime.
     */
    boolean existsByServiceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Long serviceId, BookingStatus status, Instant newEndTime, Instant newStartTime);

    /** Returns all bookings whose service is in the given ID list. */
    List<Booking> findByServiceIdIn(List<Long> serviceIds);
}