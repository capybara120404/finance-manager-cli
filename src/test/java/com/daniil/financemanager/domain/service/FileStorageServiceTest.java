package com.daniil.financemanager.domain.service;

import com.daniil.financemanager.domain.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {
    private FileStorageService fileStorageService;
    private User user;
    private Path storageDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        user = new User("capybara120404", "qwerty");
        storageDir = Path.of("data");
    }

    @AfterEach
    void tearDown() throws Exception {
        Path jsonFile = storageDir.resolve(user.getLogin() + ".json");
        Path csvFile = storageDir.resolve(user.getLogin() + "_report.csv");

        Files.deleteIfExists(jsonFile);
        Files.deleteIfExists(csvFile);
    }

    @Test
    void testSaveAndLoadUserData() {
        Wallet wallet = user.getWallet();
        Category food = new Category("Food");
        Transaction t = new Transaction(TransactionType.EXPENSE, food, 100, "Lunch");
        wallet.addTransaction(t);

        fileStorageService.saveUserData(user);

        Wallet loadedWallet = fileStorageService.loadUserData(user);

        assertEquals(1, loadedWallet.getTransactions().size());
        assertEquals("Lunch", loadedWallet.getTransactions().getFirst().getDescription());
        assertEquals(100, loadedWallet.getTransactions().getFirst().getAmount());
    }

    @Test
    void testLoadUserDataWhenFileNotExists() {
        Wallet loadedWallet = fileStorageService.loadUserData(user);
        assertNotNull(loadedWallet);
        assertEquals(user, loadedWallet.getUser());
        assertTrue(loadedWallet.getTransactions().isEmpty());
    }

    @Test
    void testExportAndImportCSV() {
        Wallet wallet = user.getWallet();
        Category salary = new Category("Salary");
        Transaction t1 = new Transaction(TransactionType.INCOME, salary, 2000, "Monthly salary");
        wallet.addTransaction(t1);
        t1.setDate(LocalDate.of(2025, 11, 6));

        fileStorageService.exportWalletToCSV(user);

        wallet.getTransactions().clear();
        assertTrue(wallet.getTransactions().isEmpty());

        Path csvFilePath = storageDir.resolve(user.getLogin() + "_report.csv");
        fileStorageService.importWalletFromCSV(user, csvFilePath.toString());

        assertEquals(1, wallet.getTransactions().size());
        Transaction imported = wallet.getTransactions().getFirst();
        assertEquals("Monthly salary", imported.getDescription());
        assertEquals(2000, imported.getAmount());
        assertEquals(TransactionType.INCOME, imported.getType());
        assertEquals("Salary", imported.getCategory().getName());
        assertEquals(LocalDate.of(2025, 11, 6), imported.getDate());
    }

    @Test
    void testImportCSVWithInvalidLine() throws Exception {
        Wallet wallet = user.getWallet();
        Path csvFilePath = storageDir.resolve(user.getLogin() + "_report.csv");
        Files.createDirectories(storageDir);
        Files.writeString(csvFilePath, "Type,Category,Amount,Description,Date\nINVALID_LINE\n");

        fileStorageService.importWalletFromCSV(user, csvFilePath.toString());

        assertTrue(wallet.getTransactions().isEmpty());
    }
}
