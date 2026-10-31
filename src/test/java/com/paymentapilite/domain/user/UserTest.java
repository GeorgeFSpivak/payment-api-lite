package com.paymentapilite.domain.user;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void shouldRejectNullInitialBalance() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new User(
                        "Micheal Spivak",
                        "12345678901",
                        "micheal@example.com",
                        "hash-test",
                        UserType.COMMON,
                        null
                )
        );
    }

    @Test
    void shouldRejectNegativeInitialBalance() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new User(
                        "Micheal Spivak",
                        "12345678901",
                        "micheal@example.com",
                        "hash-test",
                        UserType.COMMON,
                        new BigDecimal("-0.01")
                )
        );
    }

    @Test
    void shouldRejectInitialBalanceThatRequiresRounding() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new User(
                        "Micheal Spivak",
                        "12345678901",
                        "micheal@example.com",
                        "hash-test",
                        UserType.COMMON,
                        new  BigDecimal("10.501")
                )
        );
    }

    @Test
    void shouldNormalizeInitialBalanceWithTrailingZeros() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.500")
        );

        assertEquals(new BigDecimal("10.50"), user.getBalance());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldRejectNullOrBlankFullName(String fullName) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new User(
                        fullName,
                        "12345678901",
                        "micheal@example.com",
                        "hash-test",
                        UserType.COMMON,
                        new BigDecimal("10.50")
                )
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldRejectNullOrBlankDocument(String document) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new User(
                        "Micheal Spivak",
                        document,
                        "micheal@example.com",
                        "hash-test",
                        UserType.COMMON,
                        new BigDecimal("10.50")
                )
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldRejectNullOrBlankEmail(String email) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new User(
                        "Micheal Spivak",
                        "12345678901",
                        email,
                        "hash-test",
                        UserType.COMMON,
                        new BigDecimal("10.50")
                )
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldRejectNullOrBlankPasswordHash(String passwordHash) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new User(
                        "Micheal Spivak",
                        "12345678901",
                        "micheal@example.com",
                        passwordHash,
                        UserType.COMMON,
                        new BigDecimal("10.50")
                )
        );
    }

    @Test
    void shouldRejectNullUserType() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new User(
                        "Micheal Spivak",
                        "12345678901",
                        "micheal@example.com",
                        "hash-test",
                        null,
                        new BigDecimal("10.50")
                )
        );
    }
}
