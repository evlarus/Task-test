package com.evlarus.ecomreturns.notification;

import com.evlarus.ecomreturns.notification.event.OrderStatusChangedEvent;
import com.evlarus.ecomreturns.notification.event.ReturnStatusChangedEvent;
import com.evlarus.ecomreturns.order.domain.OrderStatus;
import com.evlarus.ecomreturns.returns.domain.ReturnStatus;
import java.util.EnumSet;
import java.util.Set;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Set<OrderStatus> URGENT_ORDER_STATUSES = EnumSet.of(OrderStatus.SHIPPED,
            OrderStatus.DELIVERED, OrderStatus.CANCELLED);
    private static final Set<ReturnStatus> URGENT_RETURN_STATUSES = EnumSet.of(ReturnStatus.APPROVED,
            ReturnStatus.REJECTED, ReturnStatus.REFUNDED);

    private final NotificationChannelFactory channelFactory;

    public NotificationService(NotificationChannelFactory channelFactory) {
        this.channelFactory = channelFactory;
    }

    @EventListener
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        String subject = "Статус заказа №" + event.orderId() + " изменён";
        String message = "Заказ №" + event.orderId() + ": " + event.oldStatus() + " -> " + event.newStatus();
        notify(event.userEmail(), subject, message, URGENT_ORDER_STATUSES.contains(event.newStatus()));
    }

    @EventListener
    public void onReturnStatusChanged(ReturnStatusChangedEvent event) {
        String subject = "Статус возврата №" + event.returnRequestId() + " изменён";
        String message = "Возврат №" + event.returnRequestId() + ": " + event.oldStatus() + " -> "
                + event.newStatus();
        notify(event.userEmail(), subject, message, URGENT_RETURN_STATUSES.contains(event.newStatus()));
    }

    private void notify(String recipient, String subject, String message, boolean urgent) {
        channelFactory.get(NotificationChannelType.EMAIL).send(recipient, subject, message);
        if (urgent) {
            channelFactory.get(NotificationChannelType.SMS).send(recipient, subject, message);
        }
    }
}
