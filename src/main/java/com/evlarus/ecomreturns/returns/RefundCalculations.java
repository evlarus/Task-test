package com.evlarus.ecomreturns.returns;

import com.evlarus.ecomreturns.returns.domain.ReturnRequest;
import java.math.BigDecimal;

class RefundCalculations {

    private RefundCalculations() {
    }

    static BigDecimal itemsTotal(ReturnRequest returnRequest) {
        return returnRequest.getItems().stream()
                .map(item -> item.getOrderItem().getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
