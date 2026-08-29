package com.evlarus.ecomreturns.payment.api;

import com.evlarus.ecomreturns.common.exception.ResourceNotFoundException;
import com.evlarus.ecomreturns.payment.PaymentService;
import com.evlarus.ecomreturns.payment.infrastructure.PaymentRepository;
import com.evlarus.ecomreturns.user.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final CurrentUserService currentUserService;

    public PaymentController(PaymentService paymentService, PaymentRepository paymentRepository,
                              CurrentUserService currentUserService) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> initiate(@PathVariable Long orderId,
                                                      @Valid @RequestBody InitiatePaymentRequest request,
                                                      Authentication authentication) {
        var user = currentUserService.resolve(authentication);
        var payment = paymentService.initiate(user, orderId, request.method());
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponse.from(payment));
    }

    @GetMapping
    public PaymentResponse get(@PathVariable Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Оплата для заказа", orderId));
    }
}
