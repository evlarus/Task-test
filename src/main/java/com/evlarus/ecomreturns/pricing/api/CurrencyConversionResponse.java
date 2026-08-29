package com.evlarus.ecomreturns.pricing.api;

import java.math.BigDecimal;

public record CurrencyConversionResponse(
        BigDecimal amount,
        String from,
        String to,
        BigDecimal converted
) {
}
