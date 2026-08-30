package com.evlarus.ecomreturns.returns.api;

import com.evlarus.ecomreturns.common.exception.ResourceNotFoundException;
import com.evlarus.ecomreturns.common.web.PageResponse;
import com.evlarus.ecomreturns.returns.ReturnService;
import com.evlarus.ecomreturns.returns.domain.ReturnRequest;
import com.evlarus.ecomreturns.returns.infrastructure.ReturnRequestRepository;
import com.evlarus.ecomreturns.user.CurrentUserService;
import com.evlarus.ecomreturns.user.domain.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ReturnController {

    private final ReturnService returnService;
    private final ReturnRequestRepository returnRequestRepository;
    private final CurrentUserService currentUserService;

    public ReturnController(ReturnService returnService, ReturnRequestRepository returnRequestRepository,
                             CurrentUserService currentUserService) {
        this.returnService = returnService;
        this.returnRequestRepository = returnRequestRepository;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/orders/{orderId}/returns")
    public ResponseEntity<ReturnResponse> create(@PathVariable Long orderId,
                                                  @Valid @RequestBody CreateReturnRequest request,
                                                  Authentication authentication) {
        User user = currentUserService.resolve(authentication);
        ReturnRequest returnRequest = returnService.create(user, orderId, request.reason(),
                request.comment(), request.items());
        return ResponseEntity.status(HttpStatus.CREATED).body(ReturnResponse.from(returnRequest));
    }

    @GetMapping("/returns/my")
    public PageResponse<ReturnResponse> myReturns(Authentication authentication,
                                                   @PageableDefault(size = 20) Pageable pageable) {
        User user = currentUserService.resolve(authentication);
        return PageResponse.of(returnRequestRepository.findByUserId(user.getId(), pageable), ReturnResponse::from);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/returns")
    public PageResponse<ReturnResponse> all(@PageableDefault(size = 20) Pageable pageable) {
        Page<ReturnRequest> page = returnRequestRepository.findAll(pageable);
        return PageResponse.of(page, ReturnResponse::from);
    }

    @GetMapping("/returns/{id}")
    public ReturnResponse get(@PathVariable Long id, Authentication authentication) {
        ReturnRequest returnRequest = returnRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Запрос на возврат", id));

        boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));
        if (!isStaff && !returnRequest.getUser().getEmail().equals(authentication.getName())) {
            throw new ResourceNotFoundException("Запрос на возврат", id);
        }

        return ReturnResponse.from(returnRequest);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/returns/{id}/approve")
    public ReturnResponse approve(@PathVariable Long id) {
        return ReturnResponse.from(returnService.approve(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/returns/{id}/reject")
    public ReturnResponse reject(@PathVariable Long id) {
        return ReturnResponse.from(returnService.reject(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/returns/{id}/receive")
    public ReturnResponse markItemReceived(@PathVariable Long id) {
        return ReturnResponse.from(returnService.markItemReceived(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/returns/{id}/refund")
    public ReturnResponse refund(@PathVariable Long id) {
        return ReturnResponse.from(returnService.refund(id));
    }
}
