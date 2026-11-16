package com.paymentapilite.domain.transfer;


import com.paymentapilite.domain.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;


@Entity
@Table(name = "transfers")
public class Transfer {
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("2.00");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("99999999999999999.99");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "sender_id",  nullable = false)
    private User sender;
    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "receiver_id",  nullable = false)
    private User receiver;
    @Column(nullable = false,  precision = 19, scale = 2)
    private BigDecimal amount;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Transfer() {
    }

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
        if  (this.amount.compareTo(MAX_AMOUNT) > 0) {
            throw new IllegalArgumentException("Amount exceeds the maximum allowed.");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null.");
        }

        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = createdAt;
    }

    public Long getId() {

        return id;
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
