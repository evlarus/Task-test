package com.evlarus.ecomreturns.user.api;

import com.evlarus.ecomreturns.user.domain.Address;

public record AddressResponse(
        Long id,
        String line1,
        String city,
        String postalCode,
        String country,
        boolean isDefault
) {
    public static AddressResponse from(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getLine1(),
                address.getCity(),
                address.getPostalCode(),
                address.getCountry(),
                address.isDefault()
        );
    }
}
