package com.evlarus.ecomreturns.payment;

import com.evlarus.ecomreturns.payment.domain.PaymentMethod;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PaymentStrategyFactory {

    private final Map<PaymentMethod, PaymentStrategy> strategies = new EnumMap<>(PaymentMethod.class);

    public PaymentStrategyFactory(List<PaymentStrategy> availableStrategies) {
        availableStrategies.forEach(strategy -> strategies.put(strategy.method(), strategy));
    }

    public PaymentStrategy get(PaymentMethod method) {
        PaymentStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new IllegalStateException("Нет реализации оплаты для метода " + method);
        }
        return strategy;
    }
}
