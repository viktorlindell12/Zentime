package com.zentime.service;

import com.zentime.dto.BookingRequest;
import com.zentime.dto.BookingResponse;
import com.zentime.model.Booking;
import com.zentime.model.BookingStatus;
import com.zentime.model.User;
import com.zentime.repository.BookingRepository;
import com.zentime.repository.BusinessRepository;
import com.zentime.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Core booking logic: creation with overlap guard, admin listing, and cancellation.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final BusinessRepository businessRepository;

    /**
     * Books a service slot for a customer.
     * <p>
     * End time is derived from the service's {@code durationMinutes} so callers
     * only need to supply a start time.
     *
     * @param request  desired service and start time
     * @param customer the booking owner
     * @return the persisted booking
     * @throws ResponseStatusException 404 if service not found, 409 if slot overlaps
     */
    public BookingResponse create(BookingRequest request, User customer) {
        var service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));

        Instant endTime = request.getStartTime().plus(service.getDurationMinutes(), ChronoUnit.MINUTES);

        // Overlap: existing.startTime < newEnd AND existing.endTime > newStart
        boolean overlap = bookingRepository
                .existsByServiceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        service.getId(), BookingStatus.BOOKED, endTime, request.getStartTime());

        if (overlap) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Time slot is already booked");
        }

        Booking booking = new Booking();
        booking.setService(service);
        booking.setCustomer(customer);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(endTime);
        bookingRepository.save(booking);

        return toResponse(booking);
    }

    /**
     * Returns all bookings for every service in the admin's business.
     *
     * @param admin the authenticated admin
     * @return bookings across all services owned by this admin
     * @throws ResponseStatusException 404 if the admin has no business registered
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllForBusiness(User admin) {
        var business = businessRepository.findByOwnerId(admin.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No business found for this admin"));

        var serviceIds = serviceRepository.findByBusinessId(business.getId())
                .stream()
                .map(s -> s.getId())
                .toList();

        return bookingRepository.findByServiceIdIn(serviceIds)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Cancels a booking.
     * <p>
     * Customers may only cancel their own bookings. Admins may cancel any booking
     * belonging to a service within their business.
     *
     * @param bookingId   the booking to cancel
     * @param currentUser the requesting user
     * @return the updated booking with status CANCELLED
     * @throws ResponseStatusException 404 if not found, 403 if not authorized, 409 if already cancelled
     */
    public BookingResponse cancel(Long bookingId, User currentUser) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        if (isAdmin) {
            var business = businessRepository.findByOwnerId(currentUser.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));
            if (!booking.getService().getBusiness().getId().equals(business.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
        } else {
            if (!booking.getCustomer().getId().equals(currentUser.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return toResponse(booking);
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getService().getId(),
                booking.getCustomer().getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}