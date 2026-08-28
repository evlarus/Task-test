package com.evlarus.ecomreturns.order;

import com.evlarus.ecomreturns.cart.domain.Cart;
import com.evlarus.ecomreturns.cart.domain.CartItem;
import com.evlarus.ecomreturns.cart.CartService;
import com.evlarus.ecomreturns.common.exception.BusinessRuleViolationException;
import com.evlarus.ecomreturns.common.exception.ResourceNotFoundException;
import com.evlarus.ecomreturns.order.domain.Order;
import com.evlarus.ecomreturns.order.domain.OrderItem;
import com.evlarus.ecomreturns.order.domain.OrderStatus;
import com.evlarus.ecomreturns.order.infrastructure.OrderRepository;
import com.evlarus.ecomreturns.user.domain.Address;
import com.evlarus.ecomreturns.user.domain.User;
import com.evlarus.ecomreturns.user.infrastructure.AddressRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final CartService cartService;
    private final OrderStatusTransitionValidator statusTransitionValidator;

    public OrderService(OrderRepository orderRepository, AddressRepository addressRepository,
                         CartService cartService, OrderStatusTransitionValidator statusTransitionValidator) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.cartService = cartService;
        this.statusTransitionValidator = statusTransitionValidator;
    }

    @Transactional
    public Order checkout(User user, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Адрес", addressId));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Адрес", addressId);
        }

        Cart cart = cartService.getOrCreateCart(user);
        if (cart.getItems().isEmpty()) {
            throw new BusinessRuleViolationException("Корзина пуста");
        }

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus(OrderStatus.NEW);
        order.setPlacedAt(Instant.now());

        var items = new ArrayList<OrderItem>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            var product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BusinessRuleViolationException("Недостаточно товара на складе: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPriceSnapshot());
            orderItem.setProductNameSnapshot(product.getName());
            items.add(orderItem);

            total = total.add(cartItem.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setItems(items);
        order.setTotalAmount(total);
        orderRepository.save(order);

        cart.getItems().clear();

        return order;
    }

    @Transactional
    public Order changeStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ", orderId));
        statusTransitionValidator.validate(order.getStatus(), newStatus);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancel(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ", orderId));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Заказ", orderId);
        }
        statusTransitionValidator.validate(order.getStatus(), OrderStatus.CANCELLED);
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}
