package com.evlarus.ecomreturns.catalog.api;

import com.evlarus.ecomreturns.catalog.domain.Product;
import com.evlarus.ecomreturns.catalog.domain.ProductImage;
import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        String currency,
        int stockQuantity,
        boolean active,
        Long categoryId,
        List<String> images
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCurrency(),
                product.getStockQuantity(),
                product.isActive(),
                product.getCategory().getId(),
                product.getImages().stream().map(ProductImage::getUrl).toList()
        );
    }
}
