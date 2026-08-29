package com.evlarus.ecomreturns.pricing;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record NbrbRateResponse(
        @JsonProperty("Cur_Abbreviation") String currencyCode,
        @JsonProperty("Cur_Scale") int scale,
        @JsonProperty("Cur_OfficialRate") BigDecimal officialRate
) {
}
