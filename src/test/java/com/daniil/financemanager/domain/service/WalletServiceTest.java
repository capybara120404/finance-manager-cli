package com.daniil.financemanager.domain.service;

import com.daniil.financemanager.domain.model.*;
import com.daniil.financemanager.domain.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WalletServiceTest {
    private WalletRepository walletRepository;
    private WalletService walletService;
    private Wallet wallet;
    private Category food;
    private Category salary;

    @BeforeEach
    void setUp() {
        walletRepository = mock(WalletRepository.class);
        walletService = new WalletService(walletRepository);
        User user = new User("capybara120404", "qwerty");
        wallet = user.getWallet();
        food = new Category("Food");
        salary = new Category("Salary");
    }

    @Test
    void testAddExpenseAndBudgetWarning() {
        Budget foodBudget = new Budget(food, 100);
        wallet.getBudgets().put(food, foodBudget);

        walletService.addExpense(wallet, 90, food, "Dinner");

        assertEquals(1, wallet.getTransactions().size());
        assertEquals(90, wallet.getTransactions().getFirst().getAmount());
        assertEquals(90, foodBudget.getSpent());

        walletService.addExpense(wallet, 20, food, "Snack");
        assertEquals(110, foodBudget.getSpent());

        verify(walletRepository, times(2)).save(wallet);
    }

    @Test
    void testAddIncomeAndTotalCalculation() {
        walletService.addIncome(wallet, 2000, salary, "Salary payment");

        assertEquals(1, wallet.getTransactions().size());
        assertEquals(2000, walletService.getTotalIncome(wallet));
        assertEquals(0, walletService.getTotalExpense(wallet));

        verify(walletRepository).save(wallet);
    }

    @Test
    void testGetTransactionsByPeriod() {
        Transaction t1 = new Transaction(TransactionType.INCOME, salary, 1000, "Salary");
        t1.setDate(LocalDate.of(2025, 11, 1));
        Transaction t2 = new Transaction(TransactionType.EXPENSE, food, 50, "Lunch");
        t2.setDate(LocalDate.of(2025, 11, 5));

        wallet.addTransaction(t1);
        wallet.addTransaction(t2);

        List<Transaction> periodTransactions = walletService.getTransactionsByPeriod(wallet,
                LocalDate.of(2025, 11, 2),
                LocalDate.of(2025, 11, 6));

        assertEquals(1, periodTransactions.size());
        assertEquals(t2, periodTransactions.getFirst());
    }

    @Test
    void testGetIncomeByCategories() {
        wallet.addTransaction(new Transaction(TransactionType.INCOME, salary, 1000, "Salary"));
        wallet.addTransaction(new Transaction(TransactionType.INCOME, new Category("Bonus"), 500, "Bonus"));
        wallet.addTransaction(new Transaction(TransactionType.EXPENSE, food, 100, "Dinner"));

        double income = walletService.getIncomeByCategories(wallet, List.of("Salary", "Bonus"));
        assertEquals(1500, income);
    }

    @Test
    void testGetExpenseByCategories() {
        wallet.addTransaction(new Transaction(TransactionType.EXPENSE, food, 100, "Lunch"));
        wallet.addTransaction(new Transaction(TransactionType.EXPENSE, new Category("Transport"), 50, "Taxi"));
        wallet.addTransaction(new Transaction(TransactionType.INCOME, salary, 1000, "Salary"));

        double expense = walletService.getExpenseByCategories(wallet, List.of("Food"));
        assertEquals(100, expense);
    }
}
