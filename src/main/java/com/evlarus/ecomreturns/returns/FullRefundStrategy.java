package com.evlarus.ecomreturns.returns;

import com.evlarus.ecomreturns.returns.domain.ReturnRequest;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

// Полный возврат — используется, когда виноват продавец (брак, не тот товар)
@Component
public class FullRefundStrategy implements RefundCalculationStrategy {

    @Override
    public BigDecimal calculate(ReturnRequest returnRequest) {
        return RefundCalculations.itemsTotal(returnRequest);
    }
}
