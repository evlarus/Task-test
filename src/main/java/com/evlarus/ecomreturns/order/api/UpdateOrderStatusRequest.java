package com.evlarus.ecomreturns.order.api;

import com.evlarus.ecomreturns.order.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull OrderStatus status
) {
}
