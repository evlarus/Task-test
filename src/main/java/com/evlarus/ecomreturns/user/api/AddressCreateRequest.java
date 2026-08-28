package com.evlarus.ecomreturns.user.api;

import jakarta.validation.constraints.NotBlank;

public record AddressCreateRequest(
        @NotBlank String line1,
        @NotBlank String city,
        @NotBlank String postalCode,
        @NotBlank String country
) {
}
