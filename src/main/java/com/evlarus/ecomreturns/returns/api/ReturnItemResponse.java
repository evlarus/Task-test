package com.evlarus.ecomreturns.returns.api;

import com.evlarus.ecomreturns.returns.domain.ReturnItem;

public record ReturnItemResponse(
        Long orderItemId,
        String productName,
        int quantity,
        String condition
) {
    public static ReturnItemResponse from(ReturnItem item) {
        return new ReturnItemResponse(
                item.getOrderItem().getId(),
                item.getOrderItem().getProductNameSnapshot(),
                item.getQuantity(),
                item.getCondition().name()
        );
    }
}
