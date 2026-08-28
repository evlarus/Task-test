package com.evlarus.ecomreturns.cart.api;

import com.evlarus.ecomreturns.cart.domain.CartItem;
import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice
) {
    public static CartItemResponse from(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPriceSnapshot()
        );
    }
}
