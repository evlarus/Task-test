package com.evlarus.ecomreturns.payment.api;

import com.evlarus.ecomreturns.payment.domain.Payment;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        String provider,
        String status,
        BigDecimal amount,
        String transactionRef,
        Instant paidAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getProvider(),
                payment.getStatus().name(),
                payment.getAmount(),
                payment.getTransactionRef(),
                payment.getPaidAt()
        );
    }
}
