package com.daniil.financemanager.domain.service;

import com.daniil.financemanager.domain.model.Budget;
import com.daniil.financemanager.domain.model.Category;
import com.daniil.financemanager.domain.model.User;
import com.daniil.financemanager.domain.model.Wallet;
import com.daniil.financemanager.domain.repository.WalletRepository;

import java.util.NoSuchElementException;

public class BudgetService {
    private final WalletRepository walletRepository;
    private final CategoryService categoryService;

    public BudgetService(WalletRepository walletRepository, CategoryService categoryService) {
        this.walletRepository = walletRepository;
        this.categoryService = categoryService;
    }

    public void setBudget(User user, String categoryName, double limit) throws IllegalArgumentException {
        if (limit <= 0) {
            throw new IllegalArgumentException("Budget limit must be a positive number.");
        }

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found for the current user."));

        Category category = categoryService.getOrCreateCategory(categoryName);

        Budget newBudget = new Budget(category, limit);
        wallet.getBudgets().put(category, newBudget);
    }

    public void updateBudget(User user, String categoryName, double newLimit) throws IllegalArgumentException, NoSuchElementException {
        if (newLimit <= 0) {
            throw new IllegalArgumentException("Budget limit must be positive.");
        }

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found."));
        Category category = categoryService.getOrCreateCategory(categoryName);

        Budget budget = wallet.getBudgets().get(category);
        if (budget == null) {
            throw new NoSuchElementException("Budget for category not found.");
        }

        wallet.getBudgets().put(category, new Budget(category, newLimit));
    }
}
