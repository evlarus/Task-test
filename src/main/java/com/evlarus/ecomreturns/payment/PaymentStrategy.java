package com.evlarus.ecomreturns.payment;

import com.evlarus.ecomreturns.payment.domain.Payment;
import com.evlarus.ecomreturns.payment.domain.PaymentMethod;

public interface PaymentStrategy {

    PaymentMethod method();

    void process(Payment payment);
}
