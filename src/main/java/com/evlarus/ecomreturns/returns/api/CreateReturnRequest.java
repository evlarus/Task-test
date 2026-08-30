package com.evlarus.ecomreturns.returns.api;

import com.evlarus.ecomreturns.returns.domain.RefundReason;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record CreateReturnRequest(
        @NotNull RefundReason reason,
        String comment,
        @NotEmpty Map<Long, Integer> items
) {
}
