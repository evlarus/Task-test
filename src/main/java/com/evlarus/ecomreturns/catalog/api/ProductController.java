package com.evlarus.ecomreturns.catalog.api;

import com.evlarus.ecomreturns.catalog.domain.Category;
import com.evlarus.ecomreturns.catalog.domain.Product;
import com.evlarus.ecomreturns.catalog.domain.ProductImage;
import com.evlarus.ecomreturns.catalog.infrastructure.CategoryRepository;
import com.evlarus.ecomreturns.catalog.infrastructure.ProductRepository;
import com.evlarus.ecomreturns.catalog.infrastructure.ProductSpecifications;
import com.evlarus.ecomreturns.common.exception.ResourceNotFoundException;
import com.evlarus.ecomreturns.common.web.PageResponse;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public PageResponse<ProductResponse> list(
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 20) Pageable pageable) {

        Specification<Product> spec = Specification.where(ProductSpecifications.isActive())
                .and(ProductSpecifications.hasCategory(category))
                .and(ProductSpecifications.nameContains(search))
                .and(ProductSpecifications.priceBetween(minPrice, maxPrice));

        return PageResponse.of(productRepository.findAll(spec, pageable), ProductResponse::from);
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return ProductResponse.from(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Товар", id)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Категория", request.categoryId()));

        Product product = new Product();
        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(category);

        if (request.images() != null) {
            var images = new ArrayList<ProductImage>();
            for (int i = 0; i < request.images().size(); i++) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setUrl(request.images().get(i));
                image.setPosition(i);
                images.add(image);
            }
            product.setImages(images);
        }

        productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(product));
    }
}
