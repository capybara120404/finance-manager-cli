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
    void testGetOrCreateCategory_WhenExists_ReturnsExisting() {
        Category existing = new Category("Food");
        when(categoryRepository.findByName("Food")).thenReturn(Optional.of(existing));

        Category result = categoryService.getOrCreateCategory("Food");

        assertSame(existing, result);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void testGetOrCreateCategory_WhenNotExists_CreatesNew() {
        when(categoryRepository.findByName("Food")).thenReturn(Optional.empty());

        Category result = categoryService.getOrCreateCategory("Food");

        assertEquals("Food", result.getName());
        verify(categoryRepository).save(result);
    }

    @Test
    void testGetOrCreateCategory_WithEmptyName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> categoryService.getOrCreateCategory(" "));
        assertThrows(IllegalArgumentException.class, () -> categoryService.getOrCreateCategory(null));
    }

    @Test
    void testUpdateCategory_SuccessfullyUpdatesTransactionsAndBudgets() {
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
    void testUpdateCategory_WhenOldCategoryNotFound_ThrowsException() {
        when(categoryRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> categoryService.updateCategory("Unknown", "New", wallet));
    }

    @Test
    void testUpdateCategory_WithEmptyNewName_ThrowsException() {
        Category existing = new Category("Food");
        when(categoryRepository.findByName("Food")).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory("Food", " ", wallet));
    }

    @Test
    void testDeleteCategory_Success() {
        Category category = new Category("Leisure");
        when(categoryRepository.findByName("Leisure")).thenReturn(Optional.of(category));

        categoryService.deleteCategory("Leisure");

        verify(categoryRepository).delete(category);
    }

    @Test
    void testDeleteCategory_NotFound_ThrowsException() {
        when(categoryRepository.findByName("Nonexistent")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> categoryService.deleteCategory("Nonexistent"));
    }
}
