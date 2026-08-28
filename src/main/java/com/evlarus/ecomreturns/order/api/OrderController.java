package com.evlarus.ecomreturns.order.api;

import com.evlarus.ecomreturns.common.exception.ResourceNotFoundException;
import com.evlarus.ecomreturns.common.web.PageResponse;
import com.evlarus.ecomreturns.order.OrderService;
import com.evlarus.ecomreturns.order.domain.Order;
import com.evlarus.ecomreturns.order.infrastructure.OrderRepository;
import com.evlarus.ecomreturns.user.CurrentUserService;
import com.evlarus.ecomreturns.user.domain.User;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final CurrentUserService currentUserService;

    public OrderController(OrderService orderService, OrderRepository orderRepository,
                            CurrentUserService currentUserService) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest request,
                                                   Authentication authentication) {
        User user = currentUserService.resolve(authentication);
        Order order = orderService.checkout(user, request.addressId());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    @GetMapping
    public PageResponse<OrderResponse> myOrders(Authentication authentication,
                                                 @PageableDefault(size = 20) Pageable pageable) {
        User user = currentUserService.resolve(authentication);
        return PageResponse.of(orderRepository.findByUserId(user.getId(), pageable), OrderResponse::from);
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id, Authentication authentication) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ", id));

        boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));
        if (!isStaff && !order.getUser().getEmail().equals(authentication.getName())) {
            throw new ResourceNotFoundException("Заказ", id);
        }

        return OrderResponse.from(order);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return OrderResponse.from(orderService.changeStatus(id, request.status()));
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable Long id, Authentication authentication) {
        User user = currentUserService.resolve(authentication);
        return OrderResponse.from(orderService.cancel(user, id));
    }
}
