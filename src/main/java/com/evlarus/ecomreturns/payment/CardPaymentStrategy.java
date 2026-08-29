package com.evlarus.ecomreturns.payment;

import com.evlarus.ecomreturns.payment.domain.Payment;
import com.evlarus.ecomreturns.payment.domain.PaymentMethod;
import com.evlarus.ecomreturns.payment.domain.PaymentStatus;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

// TODO: сейчас просто имитирует успешную оплату, интеграция с реальным платёжным шлюзом позже
@Component
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod method() {
        return PaymentMethod.CARD;
    }

    @Override
    public void process(Payment payment) {
        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setTransactionRef(UUID.randomUUID().toString());
        payment.setPaidAt(Instant.now());
    }
}
