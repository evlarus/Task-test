package com.evlarus.ecomreturns.payment.api;

import com.evlarus.ecomreturns.payment.domain.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record InitiatePaymentRequest(
        @NotNull PaymentMethod method
) {
}
