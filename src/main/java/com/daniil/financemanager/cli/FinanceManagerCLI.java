package com.daniil.financemanager.cli;

import com.daniil.financemanager.domain.model.*;
import com.daniil.financemanager.domain.service.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FinanceManagerCLI {
    private final UserService userService;
    private final WalletService walletService;
    private final BudgetService budgetService;
    private final CategoryService categoryService;
    private final FileStorageService fileStorageService;
    private final Scanner scanner;
    private User currentUser;
    private Wallet currentWallet;

    public FinanceManagerCLI(UserService userService, WalletService walletService,
                             BudgetService budgetService, CategoryService categoryService,
                             FileStorageService fileStorageService) {
        this.userService = userService;
        this.walletService = walletService;
        this.budgetService = budgetService;
        this.categoryService = categoryService;
        this.fileStorageService = fileStorageService;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Welcome to Finance Manager CLI. Type 'help' for commands.");
        boolean running = true;
        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty()) {
                continue;
            }

            try {
                switch (input) {
                    case "signup" -> handleSignUp();
                    case "login" -> handleLogin();
                    case "add_income" -> handleAddIncome();
                    case "add_expense" -> handleAddExpense();
                    case "show_balance" -> handleShowBalance();
                    case "show_budget" -> handleShowBudget();
                    case "show_transactions" -> handleShowTransactions();
                    case "add_category" -> handleAddCategory();
                    case "update_category" -> handleUpdateCategory();
                    case "delete_category" -> handleDeleteCategory();
                    case "set_budget" -> handleSetBudget();
                    case "update_budget" -> handleUpdateBudget();
                    case "stats_categories" -> handleStatsCategories();
                    case "export_csv" -> handleExportCSV();
                    case "import_csv" -> handleImportCSV();
                    case "help" -> showHelp();
                    case "exit" -> {
                        handleExit();
                        running = false;
                    }
                    default -> System.out.println("Unknown command. Type 'help' for commands.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handleSignUp() {
        System.out.print("Enter login: ");
        String login = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        try {
            userService.signUp(login, password);
            System.out.println("User registered successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleLogin() {
        System.out.print("Enter login: ");
        String login = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        boolean success = userService.signIn(login, password);
        if (success) {
            currentUser = userService.findUserByLogin(login);
            currentWallet = fileStorageService.loadUserData(currentUser);
            System.out.println("Logged in successfully.");
        } else {
            System.out.println("Invalid login or password.");
        }
    }

    private void handleAddIncome() {
        if (checkLogin()) {
            return;
        }

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter category: ");
        String categoryName = scanner.nextLine().trim();
        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();

        Category category = categoryService.getOrCreateCategory(categoryName);
        walletService.addIncome(currentWallet, amount, category, description);
        System.out.println("Income added.");
    }

    private void handleAddExpense() {
        if (checkLogin()) {
            return;
        }

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter category: ");
        String categoryName = scanner.nextLine().trim();
        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();

        Category category = categoryService.getOrCreateCategory(categoryName);
        walletService.addExpense(currentWallet, amount, category, description);
        System.out.println("Expense added.");
    }

    private void handleShowBalance() {
        if (checkLogin()) {
            return;
        }

        double totalIncome = walletService.getTotalIncome(currentWallet);
        double totalExpense = walletService.getTotalExpense(currentWallet);
        System.out.printf("Total Income: %.2f\nTotal Expense: %.2f\nBalance: %.2f\n",
                totalIncome, totalExpense, totalIncome - totalExpense);
    }

    private void handleStatsCategories() {
        if (checkLogin()) {
            return;
        }

        System.out.print("Enter categories (comma-separated): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("No categories provided.");
            return;
        }

        List<String> categories = Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        if (categories.isEmpty()) {
            System.out.println("Invalid categories.");
            return;
        }

        double income = walletService.getIncomeByCategories(currentWallet, categories);
        double expense = walletService.getExpenseByCategories(currentWallet, categories);
        if (income == 0 && expense == 0) {
            System.out.println("No data for selected categories.");
        } else {
            System.out.printf("Income: %.2f\nExpense: %.2f\n", income, expense);
        }
    }

    private void handleShowBudget() {
        if (checkLogin()) {
            return;
        }

        Map<Category, Budget> budgets = currentWallet.getBudgets();
        if (budgets.isEmpty()) {
            System.out.println("No budgets set.");
            return;
        }
        System.out.println("Budgets:");
        budgets.forEach((category, budget) -> {
            double remaining = budget.getLimit() - budget.getSpent();
            System.out.printf("%s: Limit %.2f, Spent %.2f, Remaining %.2f\n",
                    category.getName(), budget.getLimit(), budget.getSpent(), remaining);
        });
    }

    private void handleShowTransactions() {
        if (checkLogin()) {
            return;
        }

        System.out.print("Filter by date? (yyyy-MM-dd yyyy-MM-dd) or enter to skip: ");
        String input = scanner.nextLine().trim();
        List<Transaction> transactions;

        if (!input.isEmpty()) {
            String[] dates = input.split("\\s+");
            if (dates.length != 2) {
                System.out.println("Invalid date input. Usage: start_date end_date (yyyy-MM-dd)");
                return;
            }
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate start = LocalDate.parse(dates[0], formatter);
                LocalDate end = LocalDate.parse(dates[1], formatter);
                if (end.isBefore(start)) {
                    System.out.println("End date cannot be before start date.");
                    return;
                }
                transactions = walletService.getTransactionsByPeriod(currentWallet, start, end);
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
                return;
            }
        } else {
            transactions = currentWallet.getTransactions();
        }

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        transactions.forEach(t -> System.out.printf("%s | %s | %.2f | %s | %s\n",
                t.getType(), t.getCategory().getName(), t.getAmount(), t.getDescription(), t.getDate()));
    }


    private void handleAddCategory() {
        if (checkLogin()) {
            return;
        }

        System.out.print("Enter category name: ");
        String name = scanner.nextLine().trim();
        categoryService.getOrCreateCategory(name);
        System.out.println("Category added.");
    }

    private void handleUpdateCategory() {
        if (checkLogin()) {
            return;
        }

        System.out.print("Enter old category name: ");
        String oldName = scanner.nextLine().trim();
        System.out.print("Enter new category name: ");
        String newName = scanner.nextLine().trim();
        categoryService.updateCategory(oldName, newName, currentWallet);
        System.out.println("Category updated.");
    }

    private void handleDeleteCategory() {
        if (checkLogin()) {
            return;
        }

        System.out.print("Enter category name to delete: ");
        String name = scanner.nextLine().trim();
        categoryService.deleteCategory(name);
        System.out.println("Category deleted.");
    }

    private void handleSetBudget() {
        if (checkLogin()) return;

        System.out.print("Enter category name: ");
        String categoryName = scanner.nextLine().trim();
        System.out.print("Enter budget limit: ");
        double limit = Double.parseDouble(scanner.nextLine());

        budgetService.setBudget(currentUser, categoryName, limit);
        currentWallet = fileStorageService.loadUserData(currentUser);
        fileStorageService.saveUserData(currentUser);
        System.out.println("Budget set.");
    }

    private void handleUpdateBudget() {
        if (checkLogin()) return;

        System.out.print("Enter category name: ");
        String categoryName = scanner.nextLine().trim();
        System.out.print("Enter new budget limit: ");
        double limit = Double.parseDouble(scanner.nextLine());

        budgetService.updateBudget(currentUser, categoryName, limit);
        currentWallet = fileStorageService.loadUserData(currentUser);
        fileStorageService.saveUserData(currentUser);
        System.out.println("Budget updated.");
    }


    private void handleExportCSV() {
        if (checkLogin()) {
            return;
        }

        fileStorageService.exportWalletToCSV(currentUser);
        System.out.println("Wallet exported to CSV.");
    }

    private void handleImportCSV() {
        if (checkLogin()) {
            return;
        }

        System.out.print("Enter CSV file path: ");
        String path = scanner.nextLine().trim();
        fileStorageService.importWalletFromCSV(currentUser, path);
        System.out.println("CSV imported successfully.");
    }

    private void handleExit() {
        if (currentUser != null) {
            fileStorageService.saveUserData(currentUser);
        }
        System.out.println("Exiting Finance Manager CLI.");
    }

    private boolean checkLogin() {
        if (currentUser == null) {
            System.out.println("You must be logged in to perform this action.");
            return true;
        }

        return false;
    }

    private void showHelp() {
        System.out.println("Available commands:");
        System.out.println("signup               - Register a new user");
        System.out.println("login                - Login as existing user");
        System.out.println("add_income           - Add income");
        System.out.println("add_expense          - Add expense");
        System.out.println("show_balance         - Show total income, expense, and balance");
        System.out.println("show_budget          - Show budgets and remaining amounts");
        System.out.println("show_transactions    - List transactions (with optional date filter)");
        System.out.println("add_category         - Add new category");
        System.out.println("update_category      - Update category name");
        System.out.println("delete_category      - Delete a category");
        System.out.println("set_budget           - Set budget for category");
        System.out.println("update_budget        - Update budget for category");
        System.out.println("export_csv           - Export wallet to CSV");
        System.out.println("import_csv           - Import CSV into wallet");
        System.out.println("help                 - Show this help");
        System.out.println("exit                 - Exit application");
    }
}
