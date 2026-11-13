package com.paymentapilite.domain.transfer;


import com.paymentapilite.domain.user.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

public class Transfer {
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("2.00");
    private User sender;
    private User receiver;
    private BigDecimal amount;
    private Instant createdAt;

    public Transfer(
            User sender,
            User receiver,
            BigDecimal amount,
            Instant createdAt
    ) {
        if (sender == null) {
            throw new IllegalArgumentException("Sender cannot be null.");
        }
        if (receiver == null) {
            throw new IllegalArgumentException("Receiver cannot be null.");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
        try {
            this.amount = amount.setScale(2, RoundingMode.UNNECESSARY);
        }  catch (ArithmeticException ex) {
            throw new IllegalArgumentException("Amount must be representable in cents.", ex);
        }
        if (this.amount.compareTo(MIN_AMOUNT) < 0) {
            throw new IllegalArgumentException("Amount must be at least 2.00.");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null.");
        }


        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = createdAt;
    }

    public User getSender() {

        return sender;
    }

    public User getReceiver() {

        return receiver;
    }

    public BigDecimal getAmount() {

        return amount;
    }

    public Instant getCreatedAt() {

        return createdAt;
    }
}
