package com.evlarus.ecomreturns.pricing.api;

import com.evlarus.ecomreturns.pricing.ExchangeRateService;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/currency")
public class CurrencyController {

    private final ExchangeRateService exchangeRateService;

    public CurrencyController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/convert")
    public CurrencyConversionResponse convert(@RequestParam BigDecimal amount,
                                               @RequestParam String from,
                                               @RequestParam String to) {
        BigDecimal converted = exchangeRateService.convert(amount, from, to);
        return new CurrencyConversionResponse(amount, from.toUpperCase(), to.toUpperCase(), converted);
    }
}
