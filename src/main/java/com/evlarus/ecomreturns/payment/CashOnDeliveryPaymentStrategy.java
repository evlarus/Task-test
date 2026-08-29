package com.evlarus.ecomreturns.payment;

import com.evlarus.ecomreturns.payment.domain.Payment;
import com.evlarus.ecomreturns.payment.domain.PaymentMethod;
import com.evlarus.ecomreturns.payment.domain.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class CashOnDeliveryPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod method() {
        return PaymentMethod.CASH_ON_DELIVERY;
    }

    @Override
    public void process(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
    }
}
