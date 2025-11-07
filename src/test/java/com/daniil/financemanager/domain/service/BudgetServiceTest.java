package com.daniil.financemanager.domain.service;

import com.daniil.financemanager.domain.model.Budget;
import com.daniil.financemanager.domain.model.Category;
import com.daniil.financemanager.domain.model.User;
import com.daniil.financemanager.domain.model.Wallet;
import com.daniil.financemanager.domain.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BudgetServiceTest {
    private WalletRepository walletRepository;
    private CategoryService categoryService;
    private BudgetService budgetService;
    private User user;
    private Wallet wallet;
    private Category foodCategory;

    @BeforeEach
    void setUp() {
        walletRepository = mock(WalletRepository.class);
        categoryService = mock(CategoryService.class);
        budgetService = new BudgetService(walletRepository, categoryService);

        user = new User("capybara120404", "qwerty");
        wallet = new Wallet(user);
        foodCategory = new Category("Food");
    }

    @Test
    void testSetBudgetSuccessfully() {
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(categoryService.getOrCreateCategory("Food")).thenReturn(foodCategory);

        budgetService.setBudget(user, "Food", 5000);

        assertTrue(wallet.getBudgets().containsKey(foodCategory));
        assertEquals(5000, wallet.getBudgets().get(foodCategory).getLimit());
    }

    @Test
    void testSetBudgetWithNegativeLimitThrowsException() {
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(categoryService.getOrCreateCategory("Food")).thenReturn(foodCategory);

        assertThrows(IllegalArgumentException.class,
                () -> budgetService.setBudget(user, "Food", -100));
    }

    @Test
    void testSetBudgetWithNoWalletThrowsException() {
        when(walletRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> budgetService.setBudget(user, "Food", 3000));
    }

    @Test
    void testUpdateBudgetSuccessfully() {
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(categoryService.getOrCreateCategory("Food")).thenReturn(foodCategory);

        wallet.getBudgets().put(foodCategory, new Budget(foodCategory, 3000));

        budgetService.updateBudget(user, "Food", 7000);

        assertEquals(7000, wallet.getBudgets().get(foodCategory).getLimit());
    }

    @Test
    void testUpdateBudgetNotFoundThrowsException() {
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(categoryService.getOrCreateCategory("Food")).thenReturn(foodCategory);

        assertThrows(NoSuchElementException.class,
                () -> budgetService.updateBudget(user, "Food", 7000));
    }

    @Test
    void testUpdateBudgetWithNegativeLimitThrowsException() {
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(categoryService.getOrCreateCategory("Food")).thenReturn(foodCategory);

        wallet.getBudgets().put(foodCategory, new Budget(foodCategory, 3000));

        assertThrows(IllegalArgumentException.class,
                () -> budgetService.updateBudget(user, "Food", -500));
    }
}
