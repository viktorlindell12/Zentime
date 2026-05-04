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

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;

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