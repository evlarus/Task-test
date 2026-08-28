package com.evlarus.ecomreturns.catalog.api;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank String name,
        @NotBlank String slug,
        Long parentId
) {
}
