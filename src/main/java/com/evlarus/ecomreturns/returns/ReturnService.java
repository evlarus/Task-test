package com.evlarus.ecomreturns.returns;

import com.evlarus.ecomreturns.common.exception.BusinessRuleViolationException;
import com.evlarus.ecomreturns.common.exception.ResourceNotFoundException;
import com.evlarus.ecomreturns.order.domain.Order;
import com.evlarus.ecomreturns.order.domain.OrderItem;
import com.evlarus.ecomreturns.order.domain.OrderStatus;
import com.evlarus.ecomreturns.order.infrastructure.OrderRepository;
import com.evlarus.ecomreturns.returns.domain.ItemCondition;
import com.evlarus.ecomreturns.returns.domain.RefundReason;
import com.evlarus.ecomreturns.returns.domain.ReturnItem;
import com.evlarus.ecomreturns.returns.domain.ReturnRequest;
import com.evlarus.ecomreturns.returns.domain.ReturnStatus;
import com.evlarus.ecomreturns.returns.infrastructure.ReturnRequestRepository;
import com.evlarus.ecomreturns.user.domain.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReturnService {

    private final ReturnRequestRepository returnRequestRepository;
    private final OrderRepository orderRepository;
    private final ReturnStatusTransitionValidator statusTransitionValidator;
    private final RefundCalculationStrategyFactory refundStrategyFactory;

    public ReturnService(ReturnRequestRepository returnRequestRepository, OrderRepository orderRepository,
                          ReturnStatusTransitionValidator statusTransitionValidator,
                          RefundCalculationStrategyFactory refundStrategyFactory) {
        this.returnRequestRepository = returnRequestRepository;
        this.orderRepository = orderRepository;
        this.statusTransitionValidator = statusTransitionValidator;
        this.refundStrategyFactory = refundStrategyFactory;
    }

    @Transactional
    public ReturnRequest create(User user, Long orderId, RefundReason reason, String comment,
                                 Map<Long, Integer> itemsToReturn) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ", orderId));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Заказ", orderId);
        }
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessRuleViolationException("Возврат возможен только для доставленного заказа");
        }

        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setOrder(order);
        returnRequest.setUser(user);
        returnRequest.setReason(reason);
        returnRequest.setComment(comment);

        var items = new ArrayList<ReturnItem>();
        for (Map.Entry<Long, Integer> entry : itemsToReturn.entrySet()) {
            OrderItem orderItem = order.getItems().stream()
                    .filter(oi -> oi.getId().equals(entry.getKey()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Позиция заказа", entry.getKey()));

            if (entry.getValue() > orderItem.getQuantity()) {
                throw new BusinessRuleViolationException(
                        "Нельзя вернуть больше, чем куплено: " + orderItem.getProductNameSnapshot());
            }

            ReturnItem returnItem = new ReturnItem();
            returnItem.setReturnRequest(returnRequest);
            returnItem.setOrderItem(orderItem);
            returnItem.setQuantity(entry.getValue());
            returnItem.setCondition(ItemCondition.NEW);
            items.add(returnItem);
        }

        returnRequest.setItems(items);
        return returnRequestRepository.save(returnRequest);
    }

    @Transactional
    public ReturnRequest approve(Long returnRequestId) {
        return transition(returnRequestId, ReturnStatus.APPROVED);
    }

    @Transactional
    public ReturnRequest reject(Long returnRequestId) {
        ReturnRequest returnRequest = transition(returnRequestId, ReturnStatus.REJECTED);
        returnRequest.setResolvedAt(Instant.now());
        return returnRequest;
    }

    @Transactional
    public ReturnRequest markItemReceived(Long returnRequestId) {
        ReturnRequest returnRequest = transition(returnRequestId, ReturnStatus.ITEM_RECEIVED);

        for (ReturnItem item : returnRequest.getItems()) {
            var product = item.getOrderItem().getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }

        return returnRequest;
    }

    @Transactional
    public ReturnRequest refund(Long returnRequestId) {
        ReturnRequest returnRequest = transition(returnRequestId, ReturnStatus.REFUNDED);
        var refundAmount = refundStrategyFactory.get(returnRequest.getReason()).calculate(returnRequest);
        returnRequest.setRefundAmount(refundAmount);
        returnRequest.setResolvedAt(Instant.now());
        return returnRequest;
    }

    private ReturnRequest transition(Long returnRequestId, ReturnStatus newStatus) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Запрос на возврат", returnRequestId));
        statusTransitionValidator.validate(returnRequest.getStatus(), newStatus);
        returnRequest.setStatus(newStatus);
        return returnRequestRepository.save(returnRequest);
    }
}
