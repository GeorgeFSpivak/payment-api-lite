package com.paymentapilite.domain.user;


import com.paymentapilite.exceptions.DebitNotAllowedException;
import com.paymentapilite.exceptions.InsufficientBalanceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void shouldCreditCommonUserBalance() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        user.credit(new BigDecimal("2.25"));

        assertEquals(new BigDecimal("12.75"), user.getBalance());
    }

    @Test
    void shouldCreditMerchantBalance() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.MERCHANT,
                new BigDecimal("0.00")
        );

        user.credit(new BigDecimal("0.01"));

        assertEquals(new BigDecimal("0.01"), user.getBalance());
    }

    @Test
    void shouldNormalizeCreditAmount() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.MERCHANT,
                new BigDecimal("10.50")
        );

        user.credit(new BigDecimal("2"));

        assertEquals(new BigDecimal("12.50"), user.getBalance());
    }

    @Test
    void shouldRejectNullCreditWithoutChangingBalance() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> user.credit(null)
        );

        assertEquals(new BigDecimal("10.50"), user.getBalance());
    }

    @Test
    void shouldRejectZeroCreditWithoutChangingBalance() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> user.credit(BigDecimal.ZERO)
        );

        assertEquals(new BigDecimal("10.50"), user.getBalance());
    }

    @Test
    void shouldRejectNegativeCreditWithoutChangingBalance() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> user.credit(new  BigDecimal("-5"))
        );

        assertEquals(new BigDecimal("10.50"), user.getBalance());
    }

    @Test
    void shouldRejectCreditthatRequiresRoundingWithoutChangingBalance() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> user.credit(new  BigDecimal("5.001"))
        );

        assertEquals(new BigDecimal("10.50"), user.getBalance());
    }

    @Test
    void shouldCreditAmountWithTrailingZeros() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        user.credit(new  BigDecimal("5.250"));

        assertEquals(new BigDecimal("15.75"), user.getBalance());
    }

    @Test
    void shouldDebitCommonUserBalance() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        user.debit(new BigDecimal("2.25"));

        assertEquals(new BigDecimal("8.25"), user.getBalance());
    }

    @Test
    void shouldAllowDebitingEntireBalance() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        user.debit(new BigDecimal("10.50"));

        assertEquals(new BigDecimal("0.00"), user.getBalance());
    }

    @Test
    void shouldRejectDebitWithInsufficientBalance() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                InsufficientBalanceException.class,
                () -> user.debit(new BigDecimal("10.51"))
        );

        assertEquals(new BigDecimal("10.50"), user.getBalance());
    }

    @Test
    void shouldRejectDebitForMerchant() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.MERCHANT,
                new BigDecimal("10.50")
        );

        assertThrows(
                DebitNotAllowedException.class,
                () -> user.debit(new BigDecimal("2.25"))
        );

        assertEquals(new BigDecimal("10.50"), user.getBalance());
    }

    @Test
    void shouldRejectNullDebitWithoutChangingBalance() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> user.debit(null)
        );

        assertEquals(new BigDecimal("10.50"), user.getBalance());
    }

    @Test
    void shouldRejectZeroDebitWithoutChangingBalance() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> user.debit(BigDecimal.ZERO)
        );

        assertEquals(new BigDecimal("10.50"), user.getBalance());
    }

    @Test
    void shouldRejectNegativeDebitWithoutChangingBalance() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> user.debit(new  BigDecimal("-5"))
        );

        assertEquals(new BigDecimal("10.50"), user.getBalance());
    }

    @Test
    void shouldRejectDebitThatRequiresRoundingWithoutChangingBalance() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> user.debit(new  BigDecimal("5.251"))
        );

        assertEquals(new BigDecimal("10.50"), user.getBalance());
    }

    @Test
    void shouldDebitWholeNumberAmount() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        user.debit(new  BigDecimal("2"));

        assertEquals(new BigDecimal("8.50"), user.getBalance());
    }

    @Test
    void shouldDebitAmountWithTrailingZeros() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        user.debit(new  BigDecimal("2.250"));

        assertEquals(new BigDecimal("8.25"), user.getBalance());
    }

    @Test
    void shouldDebitOneCent() {
        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        user.debit(new  BigDecimal("0.01"));

        assertEquals(new BigDecimal("10.49"), user.getBalance());
    }

    @Test
    void shouldAcceptFullNameAtMaximumLength() {
        String fullName = "a".repeat(150);

        User user = new User(
                fullName,
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertEquals(fullName, user.getFullName());
    }

    @Test
    void shouldRejectFullNameExceedingMaximumLength() {
        String fullName = "a".repeat(151);

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

    @Test
    void shouldAcceptDocumentAtMaximumLength() {
        String document = "1".repeat(20);

        User user = new User(
                "Micheal Spivak",
                document,
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertEquals(document, user.getDocument());
    }

    @Test
    void shouldRejectDocumentExceedingMaximumLength() {
        String document = "1".repeat(21);

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

    @Test
    void shouldAcceptEmailAtMaximumLength() {
        String email = "a".repeat(255);

        User user = new User(
                "Micheal Spivak",
                "12345678901",
                email,
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertEquals(email, user.getEmail());
    }

    @Test
    void shouldRejectEmailExceedingMaximumLength() {
        String email = "a".repeat(256);

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

    @Test
    void shouldAcceptPasswordHashAtMaximumLength() {
        String passwordHash = "a".repeat(255);

        assertDoesNotThrow(
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
    void shouldRejectPasswordHashExceedingMaximumLength() {
        String passwordHash = "a".repeat(256);

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
    void shouldAcceptMaximumInitialBalance() {
        BigDecimal balance = new BigDecimal("9".repeat(17) + ".99");

        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                balance
        );

        assertEquals(balance, user.getBalance());
    }

    @Test
    void shouldRejectInitialBalanceExceedingMaximum() {
        BigDecimal balance = new BigDecimal("1" + "0".repeat(17) + ".00");

        assertThrows(
                IllegalArgumentException.class,
                () -> new User(
                        "Micheal Spivak",
                        "12345678901",
                        "micheal@example.com",
                        "hash-test",
                        UserType.COMMON,
                        balance
                )
        );
    }

    @Test
    void shouldAllowCreditReachingMaximumBalance() {
        BigDecimal balance = new BigDecimal("9".repeat(17) + ".98");

        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                balance
        );


        user.credit(new BigDecimal("0.01"));

        assertEquals(new BigDecimal("9".repeat(17) + ".99"), user.getBalance());
    }

    @Test
    void shouldRejectCreditExceedingMaximumWithoutChangingBalance() {
        BigDecimal balance = new BigDecimal("9".repeat(17) + ".99");

        User user = new User(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "hash-test",
                UserType.COMMON,
                balance
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> user.credit(new BigDecimal("0.01"))
        );

        assertEquals(balance, user.getBalance());
    }
}
