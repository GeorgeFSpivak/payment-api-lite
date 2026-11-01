package com.paymentapilite.exceptions;



public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException() {

        super("Insufficient balance.");
    }
}
