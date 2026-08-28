package com.evlarus.ecomreturns.catalog.api;

import com.evlarus.ecomreturns.catalog.domain.Product;
import com.evlarus.ecomreturns.catalog.domain.Review;
import com.evlarus.ecomreturns.catalog.infrastructure.ProductRepository;
import com.evlarus.ecomreturns.catalog.infrastructure.ReviewRepository;
import com.evlarus.ecomreturns.common.exception.ResourceNotFoundException;
import com.evlarus.ecomreturns.common.web.PageResponse;
import com.evlarus.ecomreturns.user.domain.User;
import com.evlarus.ecomreturns.user.infrastructure.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewController(ReviewRepository reviewRepository, ProductRepository productRepository,
                             UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public PageResponse<ReviewResponse> list(@PathVariable Long productId,
                                              @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.of(reviewRepository.findByProductId(productId, pageable), ReviewResponse::from);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@PathVariable Long productId,
                                                  @Valid @RequestBody ReviewCreateRequest request,
                                                  Authentication authentication) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Товар", productId));
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", authentication.getName()));

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.rating());
        review.setComment(request.comment());
        reviewRepository.save(review);

        return ResponseEntity.status(HttpStatus.CREATED).body(ReviewResponse.from(review));
    }
}
