package com.daniil.financemanager.domain.service;

import com.daniil.financemanager.domain.model.*;
import com.daniil.financemanager.domain.repository.WalletRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class WalletService {
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void addExpense(Wallet wallet, double amount, Category category, String description) {
        Transaction transaction = new Transaction(TransactionType.EXPENSE, category, amount, description);
        wallet.addTransaction(transaction);

        Budget budget = wallet.getBudgets().get(category);
        if (budget != null) {
            budget.addExpense(amount);
            double spent = budget.getSpent();
            double limit = budget.getLimit();

            if (spent > limit) {
                System.out.printf("Budget exceeded for category '%s': Spent %.2f / Limit %.2f\n",
                        category.getName(), spent, limit);
            } else if (spent >= 0.8 * limit) {
                System.out.printf("Approaching budget limit for category '%s': Spent %.2f / Limit %.2f\n",
                        category.getName(), spent, limit);
            }
        }

        double totalIncome = getTotalIncome(wallet);
        double totalExpense = getTotalExpense(wallet);
        if (totalExpense > totalIncome) {
            System.out.printf("Warning: Total expenses (%.2f) exceed total income (%.2f)!\n",
                    totalExpense, totalIncome);
        }

        if (totalIncome - totalExpense <= 0) {
            System.out.println("Notice: Your wallet balance is zero or negative!");
        }

        walletRepository.save(wallet);
    }

    public void addIncome(Wallet wallet, double amount, Category category, String description) {
        Transaction transaction = new Transaction(TransactionType.INCOME, category, amount, description);
        wallet.addTransaction(transaction);

        double totalIncome = getTotalIncome(wallet);
        double totalExpense = getTotalExpense(wallet);
        if (totalExpense > totalIncome) {
            System.out.printf("Warning: Total expenses (%.2f) exceed total income (%.2f)!\n",
                    totalExpense, totalIncome);
        }

        walletRepository.save(wallet);
    }

    public double getTotalIncome(Wallet wallet) {
        return wallet.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpense(Wallet wallet) {
        return wallet.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public List<Transaction> getTransactionsByPeriod(Wallet wallet, LocalDate start, LocalDate end) {
        return wallet.getTransactions().stream()
                .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                .collect(Collectors.toList());
    }

    public double getIncomeByCategories(Wallet wallet, List<String> categories) {
        return wallet.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.INCOME && categories.contains(t.getCategory().getName()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getExpenseByCategories(Wallet wallet, List<String> categories) {
        return wallet.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE && categories.contains(t.getCategory().getName()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}
