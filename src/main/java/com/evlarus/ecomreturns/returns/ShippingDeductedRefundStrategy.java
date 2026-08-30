package com.evlarus.ecomreturns.returns;

import com.evlarus.ecomreturns.returns.domain.ReturnRequest;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

// Возврат за вычетом стоимости доставки — когда покупатель просто передумал
@Component
public class ShippingDeductedRefundStrategy implements RefundCalculationStrategy {

    private static final BigDecimal SHIPPING_DEDUCTION = BigDecimal.valueOf(5.00);

    @Override
    public BigDecimal calculate(ReturnRequest returnRequest) {
        BigDecimal total = RefundCalculations.itemsTotal(returnRequest).subtract(SHIPPING_DEDUCTION);
        return total.max(BigDecimal.ZERO);
    }
}
