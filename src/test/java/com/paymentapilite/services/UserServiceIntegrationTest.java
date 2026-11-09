package com.paymentapilite.services;


import com.paymentapilite.domain.user.User;
import com.paymentapilite.domain.user.UserRequestDTO;
import com.paymentapilite.domain.user.UserResponseDTO;
import com.paymentapilite.domain.user.UserType;
import com.paymentapilite.exceptions.UserAlreadyExistsException;
import com.paymentapilite.exceptions.UserNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {
    @Autowired
    private UserService service;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldCreateUserWithEncoderPassword() {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        UserResponseDTO response = service.createUser(request);

        assertNotNull(response.id());
        assertEquals(request.fullName(), response.fullName());
        assertEquals(request.userType(), response.userType());
        assertEquals(request.balance(), response.balance());

        entityManager.flush();
        entityManager.clear();

        User saved = entityManager.find(User.class, response.id());

        assertNotNull(saved);
        assertEquals(request.fullName(), saved.getFullName());
        assertEquals(request.document(), saved.getDocument());
        assertEquals(request.email(), saved.getEmail());
        assertEquals(request.userType(), saved.getUserType());
        assertEquals(request.balance(), saved.getBalance());

        String storedHash = (String) entityManager.createNativeQuery(
                "SELECT password_hash FROM users WHERE id = ?"
        ).setParameter(1, response.id()).getSingleResult();

        assertNotEquals(request.password(), storedHash);
        assertTrue(passwordEncoder.matches(request.password(), storedHash));
    }

    @Test
    void shouldRejectDuplicateDocument() {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        UserRequestDTO duplicateRequest = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                "other@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        service.createUser(request);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> service.createUser(duplicateRequest)
        );
    }

    @Test
    void shouldRejectDuplicateEmail() {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        UserRequestDTO duplicateRequest = new UserRequestDTO(
                "Micheal Spivak",
                "12345678902",
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        service.createUser(request);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> service.createUser(duplicateRequest)
        );
    }

    @Test
    void shouldFindUserById() {
        UserRequestDTO request = new UserRequestDTO(
                "Micheal Spivak",
                "12345678901",
                "micheal@example.com",
                "test-password",
                UserType.COMMON,
                new BigDecimal("10.50")
        );

        UserResponseDTO created = service.createUser(request);

        entityManager.flush();
        entityManager.clear();

        UserResponseDTO found = service.getUserById(created.id());

        assertEquals(created.id(), found.id());
        assertEquals(created.fullName(), found.fullName());
        assertEquals(created.userType(), found.userType());
        assertEquals(created.balance(), found.balance());
    }

    @Test
    void shouldRejectUnknownUserId() {
        Long unknownId = Long.MAX_VALUE;

        assertThrows(
                UserNotFoundException.class,
                () -> service.getUserById(unknownId)
        );
    }
}
