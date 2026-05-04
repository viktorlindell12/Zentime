package com.zentime.service;

import com.zentime.dto.BusinessRequest;
import com.zentime.dto.BusinessResponse;
import com.zentime.model.Business;
import com.zentime.model.User;
import com.zentime.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Business management: creation (one per admin) and lookup.
 */
@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;

    /**
     * Creates a business for the given admin.
     *
     * @param request business name
     * @param owner   the admin becoming the owner
     * @return the persisted business
     * @throws ResponseStatusException 409 if the admin already owns a business
     */
    public BusinessResponse create(BusinessRequest request, User owner) {
        if (businessRepository.findByOwnerId(owner.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Owner already has a business");
        }
        Business business = new Business();
        business.setName(request.getName());
        business.setOwner(owner);
        businessRepository.save(business);
        return toResponse(business);
    }

    /**
     * Fetches a business by ID.
     *
     * @param id the business ID
     * @return the matching business
     * @throws ResponseStatusException 404 if not found
     */
    public BusinessResponse getById(Long id) {
        return businessRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));
    }

    private BusinessResponse toResponse(Business business) {
        return new BusinessResponse(
                business.getId(),
                business.getName(),
                business.getOwner().getId(),
                business.getCreatedAt()
        );
    }
}