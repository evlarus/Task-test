package com.evlarus.ecomreturns.returns;

import com.evlarus.ecomreturns.returns.domain.RefundReason;
import org.springframework.stereotype.Component;

@Component
public class RefundCalculationStrategyFactory {

    private final FullRefundStrategy fullRefundStrategy;
    private final ShippingDeductedRefundStrategy shippingDeductedRefundStrategy;

    public RefundCalculationStrategyFactory(FullRefundStrategy fullRefundStrategy,
                                             ShippingDeductedRefundStrategy shippingDeductedRefundStrategy) {
        this.fullRefundStrategy = fullRefundStrategy;
        this.shippingDeductedRefundStrategy = shippingDeductedRefundStrategy;
    }

    public RefundCalculationStrategy get(RefundReason reason) {
        return switch (reason) {
            case DEFECTIVE, WRONG_ITEM -> fullRefundStrategy;
            case CHANGED_MIND, OTHER -> shippingDeductedRefundStrategy;
        };
    }
}
