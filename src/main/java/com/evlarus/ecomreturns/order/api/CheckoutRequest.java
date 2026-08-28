package com.evlarus.ecomreturns.order.api;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull Long addressId
) {
}
