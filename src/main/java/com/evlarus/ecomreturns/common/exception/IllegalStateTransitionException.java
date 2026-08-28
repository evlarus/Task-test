package com.evlarus.ecomreturns.common.exception;

import org.springframework.http.HttpStatus;

public class IllegalStateTransitionException extends DomainException {

    public IllegalStateTransitionException(String entity, Object from, Object to) {
        super("Недопустимый переход статуса %s: %s -> %s".formatted(entity, from, to));
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
