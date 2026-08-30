package com.evlarus.ecomreturns.returns;

import com.evlarus.ecomreturns.returns.domain.ReturnRequest;
import java.math.BigDecimal;

public interface RefundCalculationStrategy {

    BigDecimal calculate(ReturnRequest returnRequest);
}
