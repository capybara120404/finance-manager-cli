package com.daniil.financemanager.infrastructure.repository;

import com.daniil.financemanager.domain.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryCategoryRepositoryTest {
    private InMemoryCategoryRepository repository;
    private Category food;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCategoryRepository();
        food = new Category("Food");
    }

    @Test
    void testSaveAndFind() {
        repository.save(food);
        Optional<Category> found = repository.findByName("Food");
        assertTrue(found.isPresent());
        assertEquals(food, found.get());
    }

    @Test
    void testFindNonExistent() {
        Optional<Category> found = repository.findByName("NonExistent");
        assertTrue(found.isEmpty());
    }

    @Test
    void testDelete() {
        repository.save(food);
        repository.delete(food);
        assertTrue(repository.findByName("Food").isEmpty());
    }

    @Test
    void testCaseInsensitive() {
        repository.save(food);
        assertTrue(repository.findByName("food").isPresent());
        assertTrue(repository.findByName("FOOD").isPresent());
    }
}
