package com.paymentapilite.exceptions;



public class DebitNotAllowedException extends RuntimeException {

    public DebitNotAllowedException() {

        super("Merchants cannot send money.");
    }
}
