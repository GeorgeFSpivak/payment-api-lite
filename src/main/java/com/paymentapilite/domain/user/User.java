package com.paymentapilite.domain.user;


import com.paymentapilite.exceptions.DebitNotAllowedException;
import com.paymentapilite.exceptions.InsufficientBalanceException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Entity
@Table(name = "users")
public class User {
    private static final BigDecimal MAX_BALANCE = new BigDecimal("99999999999999999.99");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name", nullable = false,  length = 150)
    private String fullName;
    @Column(name = "document", nullable = false, unique = true,  length = 20)
    private String document;
    @Column(name = "email",  nullable = false, unique = true,  length = 255)
    private String email;
    @Column(name = "password_hash",  nullable = false,  length = 255)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false,  length = 8)
    private UserType userType;
    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    protected User() {
    }

    public User(
            String fullName,
            String document,
            String email,
            String passwordHash,
            UserType userType,
            BigDecimal balance
    ) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be null or blank.");
        }
        if (fullName.length() > 150) {
            throw new IllegalArgumentException("Full name cannot exceed 150 characters.");
        }
        if (document == null || document.isBlank()) {
            throw new IllegalArgumentException("Document cannot be null or blank.");
        }
        if (document.length() > 20) {
            throw new IllegalArgumentException("Document cannot exceed 20 characters.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank.");
        }
        if (email.length() > 255) {
            throw new IllegalArgumentException("Email cannot exceed 255 characters.");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank.");
        }
        if (passwordHash.length() > 255) {
            throw new IllegalArgumentException("Password hash cannot exceed 255 characters.");
        }
        if (userType == null) {
            throw new IllegalArgumentException("UserType cannot be null.");
        }
        if (balance == null) {
            throw new IllegalArgumentException("Balance cannot be null.");
        }
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative.");
        }
        try {
            this.balance = balance.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException("Balance must be representable in cents.", ex);
        }
        if (this.balance.compareTo(MAX_BALANCE) > 0) {
            throw new IllegalArgumentException("Balance exceeds the maximum allowed.");
        }

        this.fullName = fullName;
        this.document = document;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userType = userType;
    }

    public void credit(BigDecimal amount) {
        BigDecimal validatedAmount;

        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        try {
            validatedAmount = amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException("Amount must be representable in cents.", ex);
        }

        BigDecimal newBalance = this.balance.add(validatedAmount);

        if (newBalance.compareTo(MAX_BALANCE) > 0) {
            throw new IllegalArgumentException("Balance exceeds the maximum allowed.");
        }

        this.balance = newBalance;
    }

    public void debit(BigDecimal amount) {
        BigDecimal validatedAmount;

        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        try {
            validatedAmount = amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException("Amount must be representable in cents.", ex);
        }
        if (userType == UserType.MERCHANT) {
            throw new DebitNotAllowedException();
        }
        if (balance.compareTo(validatedAmount) < 0) {
            throw new InsufficientBalanceException();
        }

        this.balance = this.balance.subtract(validatedAmount);
    }

    public Long getId() {

        return id;
    }

    public String getFullName() {

        return fullName;
    }

    public String getDocument() {

        return document;
    }

    public String getEmail() {

        return email;
    }

    public UserType getUserType() {

        return userType;
    }

    public BigDecimal getBalance() {

        return balance;
    }

}
