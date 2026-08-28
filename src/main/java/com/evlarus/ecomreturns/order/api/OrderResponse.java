package com.evlarus.ecomreturns.order.api;

import com.evlarus.ecomreturns.order.domain.Order;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String status,
        BigDecimal totalAmount,
        String currency,
        Instant placedAt,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getPlacedAt(),
                order.getItems().stream().map(OrderItemResponse::from).toList()
        );
    }
}
