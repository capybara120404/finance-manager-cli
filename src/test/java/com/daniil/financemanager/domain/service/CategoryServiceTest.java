package com.daniil.financemanager.domain.service;

import com.daniil.financemanager.domain.model.*;
import com.daniil.financemanager.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {
    private CategoryRepository categoryRepository;
    private CategoryService categoryService;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryService = new CategoryService(categoryRepository);

        User user = new User("capybara120404", "qwerty");
        wallet = new Wallet(user);
    }

    @Test
    void testGetOrCreateCategoryWhenExistsReturnsExisting() {
        Category existing = new Category("Food");
        when(categoryRepository.findByName("Food")).thenReturn(Optional.of(existing));

        Category result = categoryService.getOrCreateCategory("Food");

        assertSame(existing, result);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void testGetOrCreateCategoryWhenNotExistsCreatesNew() {
        when(categoryRepository.findByName("Food")).thenReturn(Optional.empty());

        Category result = categoryService.getOrCreateCategory("Food");

        assertEquals("Food", result.getName());
        verify(categoryRepository).save(result);
    }

    @Test
    void testGetOrCreateCategoryWithEmptyNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> categoryService.getOrCreateCategory(" "));
        assertThrows(IllegalArgumentException.class, () -> categoryService.getOrCreateCategory(null));
    }

    @Test
    void testUpdateCategorySuccessfullyUpdatesTransactionsAndBudgets() {
        Category oldCategory = new Category("Food");
        Category newCategory = new Category("Groceries");

        when(categoryRepository.findByName("Food")).thenReturn(Optional.of(oldCategory));

        Transaction t1 = new Transaction(TransactionType.EXPENSE, oldCategory, 100, "Lunch");
        wallet.addTransaction(t1);
        Budget budget = new Budget(oldCategory, 5000);
        wallet.getBudgets().put(oldCategory, budget);

        categoryService.updateCategory("Food", "Groceries", wallet);

        assertTrue(wallet.getTransactions().stream()
                .anyMatch(t -> t.getCategory().getName().equals("Groceries")));

        assertTrue(wallet.getBudgets().containsKey(newCategory));

        verify(categoryRepository).delete(oldCategory);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testUpdateCategoryWhenOldCategoryNotFoundThrowsException() {
        when(categoryRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> categoryService.updateCategory("Unknown", "New", wallet));
    }

    @Test
    void testUpdateCategoryWithEmptyNewNameThrowsException() {
        Category existing = new Category("Food");
        when(categoryRepository.findByName("Food")).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory("Food", " ", wallet));
    }

    @Test
    void testDeleteCategorySuccess() {
        Category category = new Category("Leisure");
        when(categoryRepository.findByName("Leisure")).thenReturn(Optional.of(category));

        categoryService.deleteCategory("Leisure");

        verify(categoryRepository).delete(category);
    }

    @Test
    void testDeleteCategoryNotFoundThrowsException() {
        when(categoryRepository.findByName("Nonexistent")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> categoryService.deleteCategory("Nonexistent"));
    }
}
