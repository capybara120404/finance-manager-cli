package com.daniil.financemanager.infrastructure.repository;

import com.daniil.financemanager.domain.model.User;
import com.daniil.financemanager.domain.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryWalletRepositoryTest {
    private InMemoryWalletRepository repository;
    private User user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWalletRepository();
        user = new User("capybara120404", "qwerty");
        wallet = user.getWallet();
    }

    @Test
    void testSaveAndFind() {
        repository.save(wallet);
        Optional<Wallet> found = repository.findByUser(user);
        assertTrue(found.isPresent());
        assertEquals(wallet, found.get());
    }

    @Test
    void testFindNonExistent() {
        Optional<Wallet> found = repository.findByUser(new User("unknown", "pass"));
        assertTrue(found.isEmpty());
    }

    @Test
    void testOverwriteWallet() {
        Wallet newWallet = new Wallet(user);
        repository.save(wallet);
        repository.save(newWallet);
        Optional<Wallet> found = repository.findByUser(user);
        assertTrue(found.isPresent());
        assertEquals(newWallet, found.get());
    }
}
