package com.zentime.controller;

import com.zentime.dto.BusinessRequest;
import com.zentime.dto.BusinessResponse;
import com.zentime.model.User;
import com.zentime.service.BusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Manages business registration and lookup. One business per admin.
 */
@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    /**
     * Creates a new business owned by the authenticated admin.
     *
     * @param request     business name
     * @param currentUser the authenticated admin who becomes the owner
     * @return 201 with the created business
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BusinessResponse> create(
            @Valid @RequestBody BusinessRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(businessService.create(request, currentUser));
    }

    /**
     * Fetches a business by its ID.
     *
     * @param id the business ID
     * @return 200 with the business, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<BusinessResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(businessService.getById(id));
    }
}