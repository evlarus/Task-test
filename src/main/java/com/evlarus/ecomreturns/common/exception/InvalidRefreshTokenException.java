package com.evlarus.ecomreturns.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends DomainException {

    public InvalidRefreshTokenException() {
        super("Refresh-токен недействителен или истёк");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
