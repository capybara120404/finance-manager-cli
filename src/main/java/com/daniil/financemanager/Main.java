package com.daniil.financemanager;

import com.daniil.financemanager.cli.FinanceManagerCLI;
import com.daniil.financemanager.domain.repository.CategoryRepository;
import com.daniil.financemanager.domain.repository.UserRepository;
import com.daniil.financemanager.domain.repository.WalletRepository;
import com.daniil.financemanager.domain.service.*;
import com.daniil.financemanager.infrastructure.repository.InMemoryCategoryRepository;
import com.daniil.financemanager.infrastructure.repository.InMemoryUserRepository;
import com.daniil.financemanager.infrastructure.repository.InMemoryWalletRepository;

public class Main {
    public static void main(String[] args) {
        WalletRepository walletRepository = new InMemoryWalletRepository();
        WalletService walletService = new WalletService(walletRepository);

        UserRepository userRepository = new InMemoryUserRepository();
        UserService userService = new UserService(userRepository, walletRepository);

        CategoryRepository categoryRepository = new InMemoryCategoryRepository();
        CategoryService categoryService = new CategoryService(categoryRepository);

        BudgetService budgetService = new BudgetService(walletRepository, categoryService);

        FileStorageService fileStorageService = new FileStorageService();

        FinanceManagerCLI cli = new FinanceManagerCLI(userService, walletService, budgetService, categoryService, fileStorageService);
        cli.run();
    }
}
