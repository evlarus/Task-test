package com.evlarus.ecomreturns.order.api;

import com.evlarus.ecomreturns.order.domain.OrderItem;
import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProductNameSnapshot(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }
}
