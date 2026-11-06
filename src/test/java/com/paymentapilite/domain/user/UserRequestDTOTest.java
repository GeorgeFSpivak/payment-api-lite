package com.paymentapilite.domain.user;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRequestDTOTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptValidRequest() {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectInvalidEmail() {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                "no-email",
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        var violations = validator.validate(request);

        assertEquals(1, violations.size());

        var violation = violations.iterator().next();

        assertEquals("email", violation.getPropertyPath().toString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldRejectNullOrBlankFullName(String fullName) {
        UserRequestDTO request = new UserRequestDTO(
                fullName,
                "12345678901",
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        var violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("fullName")
                && violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(NotBlank.class)
        ));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldRejectNullOrBlankDocument(String document) {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                document,
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        var violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("document")
                        && violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(NotBlank.class)
        ));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldRejectNullOrBlankEmail(String email) {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                email,
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        var violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("email")
                        && violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(NotBlank.class)
        ));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldRejectNullOrBlankPassword(String password) {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                password,
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        var violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("password")
                        && violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(NotBlank.class)
        ));
    }

    @Test
    void shouldRejectNullUserType() {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "test-password",
                null,
                new BigDecimal("10.50")
        );

        var violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("userType")
                        && violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(NotNull.class)
        ));
    }

    @Test
    void shouldRejectNullBalance() {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                null
        );

        var violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("balance")
                        && violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(NotNull.class)
        ));
    }

    @Test
    void shouldAcceptZeroBalance() {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("0.00")
        );

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectNegativeBalance() {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("-0.01")
        );

        var violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("balance")
                        && violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(PositiveOrZero.class)
        ));
    }

    @Test
    void shouldAcceptMaximumBalance() {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("9".repeat(17) + ".99")
        );

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectBalanceExceedMax() {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("1" + "0".repeat(17) + ".00")
        );

        var violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("balance")
                        && violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(DecimalMax.class)
        ));
    }

    @Test
    void shouldAcceptFullNameMaximumLength() {
        UserRequestDTO request = new UserRequestDTO(
                "a".repeat(150),
                "12345678901",
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectFullNameExceedingMaximumLength() {
        UserRequestDTO request = new UserRequestDTO(
                "a".repeat(151),
                "12345678901",
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        var violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("fullName")
                        && violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(Size.class)
        ));
    }

    @Test
    void shouldAcceptDocumentMaximumLength() {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "1".repeat(20),
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectDocumentExceedingMaximumLength() {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "1".repeat(21),
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        var violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("document")
                        && violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(Size.class)
        ));
    }

    @Test
    void shouldAcceptEmailMaximumLength() {
        String domain = "b".repeat(63) + "." + "c".repeat(63) + "." + "d".repeat(59) + ".com";
        String email = "a".repeat(63) + "@" + domain;

        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                email,
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectEmailExceedingMaximumLength() {
        String domain = "b".repeat(63) + "." + "c".repeat(63) + "." + "d".repeat(59) + ".com";
        String email = "a".repeat(64) + "@" + domain;

        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                email,
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        var violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("email")
                        && violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(Size.class)
        ));
    }
}
