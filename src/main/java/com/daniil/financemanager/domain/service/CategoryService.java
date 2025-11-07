package com.daniil.financemanager.domain.service;

import com.daniil.financemanager.domain.model.Budget;
import com.daniil.financemanager.domain.model.Category;
import com.daniil.financemanager.domain.model.Transaction;
import com.daniil.financemanager.domain.model.Wallet;
import com.daniil.financemanager.domain.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category getOrCreateCategory(String name) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        String normalizedName = name.trim();

        return categoryRepository.findByName(normalizedName)
                .orElseGet(() -> {
                    Category newCategory = new Category(normalizedName);
                    categoryRepository.save(newCategory);
                    return newCategory;
                });
    }

    public void updateCategory(String oldName, String newName, Wallet wallet) throws IllegalArgumentException {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("New category name cannot be empty.");
        }

        Category oldCategory = categoryRepository.findByName(oldName)
                .orElseThrow(() -> new NoSuchElementException("Category not found: " + oldName));

        Category updatedCategory = new Category(newName.trim());

        List<Transaction> updatedTransactions = new ArrayList<>();
        for (Transaction t : wallet.getTransactions()) {
            if (t.getCategory().getName().equals(oldName)) {
                updatedTransactions.add(new Transaction(
                        t.getType(),
                        updatedCategory,
                        t.getAmount(),
                        t.getDescription()
                ));
            } else {
                updatedTransactions.add(t);
            }
        }
        wallet.getTransactions().clear();
        wallet.getTransactions().addAll(updatedTransactions);

        if (wallet.getBudgets().containsKey(oldCategory)) {
            Budget budget = wallet.getBudgets().remove(oldCategory);
            wallet.getBudgets().put(updatedCategory, budget);
        }

        categoryRepository.delete(oldCategory);
        categoryRepository.save(updatedCategory);
    }

    public void deleteCategory(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("Category not found."));
        categoryRepository.delete(category);
    }
}
