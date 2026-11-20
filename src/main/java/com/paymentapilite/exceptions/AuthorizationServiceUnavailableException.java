package com.paymentapilite.exceptions;

public class AuthorizationServiceUnavailableException extends RuntimeException {

    public AuthorizationServiceUnavailableException(String message) {

        super(message);
    }

    public AuthorizationServiceUnavailableException(String message, Throwable cause) {

        super(message, cause);
    }
}
