package com.evlarus.ecomreturns.order;

import com.evlarus.ecomreturns.common.exception.IllegalStateTransitionException;
import com.evlarus.ecomreturns.order.domain.OrderStatus;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusTransitionValidator {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(OrderStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(OrderStatus.NEW, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.PAID, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.PAID, EnumSet.of(OrderStatus.SHIPPED));
        ALLOWED_TRANSITIONS.put(OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED));
        ALLOWED_TRANSITIONS.put(OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class));
        ALLOWED_TRANSITIONS.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
    }

    public void validate(OrderStatus from, OrderStatus to) {
        if (!ALLOWED_TRANSITIONS.getOrDefault(from, Set.of()).contains(to)) {
            throw new IllegalStateTransitionException("Order", from, to);
        }
    }
}
