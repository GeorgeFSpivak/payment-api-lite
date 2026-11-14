package com.paymentapilite.domain.transfer;


import com.paymentapilite.domain.user.User;
import com.paymentapilite.domain.user.UserType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransferTest {

    @Test
    void shouldCreateTransferAndNormalizeAmount() {
        Instant createdAt = Instant.parse("2026-01-20T00:00:00.00Z");

        User firstUser = new User(
                "First User",
                "12345678901",
                "first@example.com",
                "-firsthash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        User secondUser = new User(
                "Second User",
                "12345678902",
                "second@example.com",
                "second-hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        Transfer transferred = new Transfer(firstUser, secondUser, new BigDecimal("2.000"), createdAt);

        assertEquals(firstUser, transferred.getSender());
        assertEquals(secondUser, transferred.getReceiver());
        assertEquals(new BigDecimal("2.00"), transferred.getAmount());
        assertEquals(createdAt, transferred.getCreatedAt());
    }

    @Test
    void shouldRejectNullSender() {
        Instant createdAt = Instant.parse("2026-01-20T00:00:00.00Z");

        User secondUser = new User(
                "Second User",
                "12345678902",
                "second@example.com",
                "second-hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transfer(null, secondUser, new BigDecimal("2.00"), createdAt)
        );
    }

    @Test
    void shouldRejectNullReceiver() {
        Instant createdAt = Instant.parse("2026-01-20T00:00:00.00Z");

        User firstUser = new User(
                "First User",
                "12345678901",
                "first@example.com",
                "-firsthash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transfer(firstUser, null, new BigDecimal("2.00"), createdAt)
        );
    }

    @Test
    void shouldRejectNullAmount() {
        Instant createdAt = Instant.parse("2026-01-20T00:00:00.00Z");

        User firstUser = new User(
                "First User",
                "12345678901",
                "first@example.com",
                "-firsthash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        User secondUser = new User(
                "Second User",
                "12345678902",
                "second@example.com",
                "second-hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transfer(firstUser, secondUser, null, createdAt)
        );
    }

    @Test
    void shouldRejectNullCreatedAt() {
        User firstUser = new User(
                "First User",
                "12345678901",
                "first@example.com",
                "-firsthash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        User secondUser = new User(
                "Second User",
                "12345678902",
                "second@example.com",
                "second-hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transfer(firstUser, secondUser, new BigDecimal("2.00"), null)
        );
    }

    @Test
    void shouldRejectAmountBelowMinimum() {
        Instant createdAt = Instant.parse("2026-01-20T00:00:00.00Z");

        User firstUser = new User(
                "First User",
                "12345678901",
                "first@example.com",
                "-firsthash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        User secondUser = new User(
                "Second User",
                "12345678902",
                "second@example.com",
                "second-hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transfer(firstUser, secondUser, new BigDecimal("1.99"), createdAt)
        );
    }

    @Test
    void shouldRejectAmountRequiringRounding() {
        Instant createdAt = Instant.parse("2026-01-20T00:00:00.00Z");

        User firstUser = new User(
                "First User",
                "12345678901",
                "first@example.com",
                "-firsthash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        User secondUser = new User(
                "Second User",
                "12345678902",
                "second@example.com",
                "second-hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transfer(firstUser, secondUser, new BigDecimal("2.001"), createdAt)
        );
    }

    @Test
    void shouldRejectNegativeAmount() {
        Instant createdAt = Instant.parse("2026-01-20T00:00:00.00Z");

        User firstUser = new User(
                "First User",
                "12345678901",
                "first@example.com",
                "-firsthash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        User secondUser = new User(
                "Second User",
                "12345678902",
                "second@example.com",
                "second-hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transfer(firstUser, secondUser, new BigDecimal("-2.00"), createdAt)
        );
    }

    @Test
    void shouldRejectZeroAmount() {
        Instant createdAt = Instant.parse("2026-01-20T00:00:00.00Z");

        User firstUser = new User(
                "First User",
                "12345678901",
                "first@example.com",
                "-firsthash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        User secondUser = new User(
                "Second User",
                "12345678902",
                "second@example.com",
                "second-hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transfer(firstUser, secondUser, BigDecimal.ZERO, createdAt)
        );
    }
}
