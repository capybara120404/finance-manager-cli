package com.daniil.financemanager.infrastructure.repository;

import com.daniil.financemanager.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryUserRepositoryTest {
    private InMemoryUserRepository repository;
    private User user;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        user = new User("capybara120404", "qwerty");
    }

    @Test
    void testSaveAndFind() {
        repository.save(user);
        Optional<User> found = repository.findByLogin("capybara120404");
        assertTrue(found.isPresent());
        assertEquals(user, found.get());
    }

    @Test
    void testFindNonExistent() {
        Optional<User> found = repository.findByLogin("unknown");
        assertTrue(found.isEmpty());
    }

    @Test
    void testOverwriteUser() {
        User updatedUser = new User("capybara120404", "newpass");
        repository.save(user);
        repository.save(updatedUser);
        Optional<User> found = repository.findByLogin("capybara120404");
        assertTrue(found.isPresent());
        assertEquals(updatedUser, found.get());
    }
}
