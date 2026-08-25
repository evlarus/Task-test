package com.evlarus.ecomreturns.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("Неверный email или пароль");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
