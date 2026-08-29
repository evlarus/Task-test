package com.evlarus.ecomreturns.pricing;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ExchangeRateClient {

    private final RestClient restClient = RestClient.create("https://api.nbrb.by");

    public NbrbRateResponse fetchRate(String currencyCode) {
        return restClient.get()
                .uri("/exrates/rates/{code}?parammode=2", currencyCode)
                .retrieve()
                .body(NbrbRateResponse.class);
    }
}
