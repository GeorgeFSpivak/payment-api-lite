package com.paymentapilite.repositories;


import com.paymentapilite.domain.user.User;
import com.paymentapilite.domain.user.UserType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;
    @PersistenceContext
    private EntityManager entityManager;

    private User newUser(String document, String email) {
        return new User(
                "Micheal Spivak",
                document,
                email,
                "hash-test",
                UserType.COMMON,
                new BigDecimal("10.50")
        );
    }

    @Test
    void shouldSaveAndReloadUser() {
        User user = newUser("12345678901", "micheal@example.com");

        User saved = repository.saveAndFlush(user);
        Long id = saved.getId();

        assertNotNull(id);

        entityManager.clear();

        User loaded = repository.findById(id).orElseThrow();

        assertEquals("Micheal Spivak", loaded.getFullName());
        assertEquals("12345678901", loaded.getDocument());
        assertEquals("micheal@example.com", loaded.getEmail());
        assertEquals(UserType.COMMON, loaded.getUserType());
        assertEquals(new BigDecimal("10.50"), loaded.getBalance());
    }

    @Test
    void shouldCheckWhetherEmailExists() {
        User user = newUser("12345678901", "micheal@example.com");

        User saved = repository.saveAndFlush(user);
        entityManager.clear();

        boolean emailExists = repository.existsByEmail(saved.getEmail());
        boolean emailNotExists = repository.existsByEmail("other@example.com");

        assertTrue(emailExists);
        assertFalse(emailNotExists);
    }

    @Test
    void shouldCheckWhetherDocumentExists() {
        User user = newUser("12345678901", "micheal@example.com");

        User saved = repository.saveAndFlush(user);
        entityManager.clear();

        boolean documentExists = repository.existsByDocument(saved.getDocument());
        boolean documentNotExists = repository.existsByDocument("12345678902");

        assertTrue(documentExists);
        assertFalse(documentNotExists);
    }

    @Test
    void shouldRejectDuplicateEmail() {
        repository.saveAndFlush(newUser("12345678901", "micheal@example.com"));

        User duplicate = newUser("12345678902", "micheal@example.com");

        assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(duplicate)
        );
    }

    @Test
    void shouldRejectDuplicateDocument() {
        repository.saveAndFlush(newUser("12345678901", "micheal@example.com"));

        User duplicate = newUser("12345678901", "other@example.com");

        assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(duplicate)
        );
    }
}
