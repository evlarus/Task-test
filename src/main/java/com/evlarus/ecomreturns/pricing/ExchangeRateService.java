package com.evlarus.ecomreturns.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService {

    private static final String BASE_CURRENCY = "BYN";

    private final ExchangeRateClient exchangeRateClient;

    public ExchangeRateService(ExchangeRateClient exchangeRateClient) {
        this.exchangeRateClient = exchangeRateClient;
    }

    @Cacheable("exchangeRates")
    public BigDecimal getRateToByn(String currencyCode) {
        if (BASE_CURRENCY.equalsIgnoreCase(currencyCode)) {
            return BigDecimal.ONE;
        }
        NbrbRateResponse rate = exchangeRateClient.fetchRate(currencyCode);
        return rate.officialRate().divide(BigDecimal.valueOf(rate.scale()), 6, RoundingMode.HALF_UP);
    }

    public BigDecimal convert(BigDecimal amount, String from, String to) {
        if (from.equalsIgnoreCase(to)) {
            return amount;
        }
        BigDecimal amountInByn = amount.multiply(getRateToByn(from));
        BigDecimal targetRate = getRateToByn(to);
        return amountInByn.divide(targetRate, 2, RoundingMode.HALF_UP);
    }
}
