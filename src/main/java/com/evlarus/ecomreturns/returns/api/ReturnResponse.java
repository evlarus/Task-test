package com.evlarus.ecomreturns.returns.api;

import com.evlarus.ecomreturns.returns.domain.ReturnRequest;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ReturnResponse(
        Long id,
        Long orderId,
        String status,
        String reason,
        String comment,
        BigDecimal refundAmount,
        Instant resolvedAt,
        List<ReturnItemResponse> items
) {
    public static ReturnResponse from(ReturnRequest returnRequest) {
        return new ReturnResponse(
                returnRequest.getId(),
                returnRequest.getOrder().getId(),
                returnRequest.getStatus().name(),
                returnRequest.getReason().name(),
                returnRequest.getComment(),
                returnRequest.getRefundAmount(),
                returnRequest.getResolvedAt(),
                returnRequest.getItems().stream().map(ReturnItemResponse::from).toList()
        );
    }
}
