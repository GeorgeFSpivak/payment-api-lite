package com.paymentapilite.domain.user;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    @Test
    void shouldCreateCommonUserWithValidData() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertEquals("Micheal Spivak", user.getFullName());
        assertEquals("12345678901", user.getDocument());
        assertEquals("micheal@example.com", user.getEmail());
        assertEquals(UserType.COMMON, user.getUserType());
        assertEquals(new BigDecimal("10.50"), user.getBalance());
    }

    @Test
    void shouldCreateMerchantWithZeroBalance() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.MERCHANT,
                BigDecimal.ZERO
        );

        assertEquals(UserType.MERCHANT, user.getUserType());
        assertEquals(new BigDecimal("0.00"), user.getBalance());
    }

    @Test
    void shouldNormalizeInitialBalanceToTwoDecimalPlaces() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10")
        );

        assertEquals(new BigDecimal("10.00"), user.getBalance());
    }
}
