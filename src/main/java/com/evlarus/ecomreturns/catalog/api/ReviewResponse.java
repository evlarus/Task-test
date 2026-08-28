package com.evlarus.ecomreturns.catalog.api;

import com.evlarus.ecomreturns.catalog.domain.Review;
import java.time.Instant;

public record ReviewResponse(
        Long id,
        int rating,
        String comment,
        String authorName,
        Instant createdAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getUser().getFirstName() + " " + review.getUser().getLastName(),
                review.getCreatedAt()
        );
    }
}
