package com.evlarus.ecomreturns.notification.event;

import com.evlarus.ecomreturns.returns.domain.ReturnStatus;

public record ReturnStatusChangedEvent(Long returnRequestId, String userEmail, ReturnStatus oldStatus,
                                        ReturnStatus newStatus) {
}
