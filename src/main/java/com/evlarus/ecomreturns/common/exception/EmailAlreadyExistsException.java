package com.evlarus.ecomreturns.common.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends DomainException {

    public EmailAlreadyExistsException(String email) {
        super("Пользователь с email=%s уже зарегистрирован".formatted(email));
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
