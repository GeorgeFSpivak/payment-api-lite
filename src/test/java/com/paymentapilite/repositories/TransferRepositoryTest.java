package com.paymentapilite.repositories;


import com.paymentapilite.domain.transfer.Transfer;
import com.paymentapilite.domain.user.User;
import com.paymentapilite.domain.user.UserType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest
public class TransferRepositoryTest {
    @Autowired
    private TransferRepository transferRepository;
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
    void shouldSaveAndReloadTransfer() {
        User sender = newUser("12345678901", "micheal@example.com");
        User receiver = newUser("12345678902", "other@example.com");
        sender = repository.saveAndFlush(sender);
        receiver = repository.saveAndFlush(receiver);

        Long senderId = sender.getId();
        Long receiverId = receiver.getId();

        Transfer transfer = new Transfer(
                sender,
                receiver,
                new BigDecimal("2.50"),
                Instant.parse("2026-01-10T12:00:00Z")
        );

        Transfer saved = transferRepository.saveAndFlush(transfer);
        Long savedId = saved.getId();

        assertNotNull(savedId);

        entityManager.clear();

        Transfer loaded = transferRepository.findById(savedId).orElseThrow();

        assertEquals(new BigDecimal("2.50"), loaded.getAmount());
        assertEquals(Instant.parse("2026-01-10T12:00:00Z"), loaded.getCreatedAt());
        assertEquals(senderId, loaded.getSender().getId());
        assertEquals(receiverId, loaded.getReceiver().getId());
    }
}
