package com.evlarus.ecomreturns.notification.event;

import com.evlarus.ecomreturns.order.domain.OrderStatus;

public record OrderStatusChangedEvent(Long orderId, String userEmail, OrderStatus oldStatus, OrderStatus newStatus) {
}
