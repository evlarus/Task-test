package com.evlarus.ecomreturns.common.exception;

import org.springframework.http.HttpStatus;

public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    public abstract HttpStatus getStatus();

    public String getErrorCode() {
        return getClass().getSimpleName();
    }
}
