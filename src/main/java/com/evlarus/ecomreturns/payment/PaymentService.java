package com.evlarus.ecomreturns.payment;

import com.evlarus.ecomreturns.common.exception.BusinessRuleViolationException;
import com.evlarus.ecomreturns.common.exception.ResourceNotFoundException;
import com.evlarus.ecomreturns.order.OrderService;
import com.evlarus.ecomreturns.order.domain.Order;
import com.evlarus.ecomreturns.order.domain.OrderStatus;
import com.evlarus.ecomreturns.order.infrastructure.OrderRepository;
import com.evlarus.ecomreturns.payment.domain.Payment;
import com.evlarus.ecomreturns.payment.domain.PaymentMethod;
import com.evlarus.ecomreturns.payment.domain.PaymentStatus;
import com.evlarus.ecomreturns.payment.infrastructure.PaymentRepository;
import com.evlarus.ecomreturns.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final PaymentStrategyFactory strategyFactory;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository,
                           OrderService orderService, PaymentStrategyFactory strategyFactory) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.strategyFactory = strategyFactory;
    }

    @Transactional
    public Payment initiate(User user, Long orderId, PaymentMethod method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ", orderId));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Заказ", orderId);
        }
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BusinessRuleViolationException("Оплата возможна только для подтверждённого заказа");
        }
        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            throw new BusinessRuleViolationException("Оплата по этому заказу уже инициирована");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setProvider(method.name());
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(PaymentStatus.PENDING);

        strategyFactory.get(method).process(payment);
        paymentRepository.save(payment);

        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            orderService.changeStatus(order.getId(), OrderStatus.PAID);
        }

        return payment;
    }
}
