package com.evlarus.ecomreturns.returns;

import com.evlarus.ecomreturns.common.exception.IllegalStateTransitionException;
import com.evlarus.ecomreturns.returns.domain.ReturnStatus;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ReturnStatusTransitionValidator {

    private static final Map<ReturnStatus, Set<ReturnStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(ReturnStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(ReturnStatus.REQUESTED, EnumSet.of(ReturnStatus.APPROVED, ReturnStatus.REJECTED));
        ALLOWED_TRANSITIONS.put(ReturnStatus.APPROVED, EnumSet.of(ReturnStatus.ITEM_RECEIVED));
        ALLOWED_TRANSITIONS.put(ReturnStatus.REJECTED, EnumSet.noneOf(ReturnStatus.class));
        ALLOWED_TRANSITIONS.put(ReturnStatus.ITEM_RECEIVED, EnumSet.of(ReturnStatus.REFUNDED));
        ALLOWED_TRANSITIONS.put(ReturnStatus.REFUNDED, EnumSet.noneOf(ReturnStatus.class));
    }

    public void validate(ReturnStatus from, ReturnStatus to) {
        if (!ALLOWED_TRANSITIONS.getOrDefault(from, Set.of()).contains(to)) {
            throw new IllegalStateTransitionException("ReturnRequest", from, to);
        }
    }
}
