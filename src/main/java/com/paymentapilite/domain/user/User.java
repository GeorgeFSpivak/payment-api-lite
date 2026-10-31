package com.paymentapilite.domain.user;


import java.math.BigDecimal;
import java.math.RoundingMode;


public class User {
    private String fullName;
    private String document;
    private String email;
    private String passwordHash;
    private UserType userType;
    private BigDecimal balance;

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
        if (document == null || document.isBlank()) {
            throw new IllegalArgumentException("Document cannot be null or blank.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank.");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank.");
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
            throw new IllegalArgumentException("Balance must be  representable in cents.", ex);
        }

        this.fullName = fullName;
        this.document = document;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userType = userType;
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
