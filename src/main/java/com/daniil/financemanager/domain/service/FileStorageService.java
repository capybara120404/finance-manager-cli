package com.daniil.financemanager.domain.service;

import com.daniil.financemanager.domain.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileStorageService {
    private static final String STORAGE_DIR = "data";
    private final ObjectMapper objectMapper;

    public FileStorageService() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private Path getUserFilePath(String login) {
        return Path.of(STORAGE_DIR, login + ".json");
    }

    public void saveUserData(User user) {
        try {
            Files.createDirectories(Path.of(STORAGE_DIR));
            Path filePath = getUserFilePath(user.getLogin());
            objectMapper.writeValue(filePath.toFile(), user.getWallet());
        } catch (IOException e) {
            throw new RuntimeException("Error while saving user data: " + e.getMessage());
        }
    }

    public Wallet loadUserData(User user) {
        Path filePath = getUserFilePath(user.getLogin());
        if (!Files.exists(filePath)) {
            Wallet wallet = new Wallet(user);
            user.getWallet().getBudgets().forEach((c, b) -> wallet.getBudgets().put(c, b));
            return wallet;
        }

        try {
            Wallet loadedWallet = objectMapper.readValue(filePath.toFile(), Wallet.class);
            loadedWallet.setUser(user);
            return loadedWallet;
        } catch (IOException e) {
            throw new RuntimeException("Error while loading user data: " + e.getMessage());
        }
    }

    public void exportWalletToCSV(User user) {
        Path filePath = Path.of(STORAGE_DIR, user.getLogin() + "_report.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write("Type,Category,Amount,Description,Date\n");
            for (Transaction t : user.getWallet().getTransactions()) {
                writer.write(String.format("%s,%s,%.2f,%s,%s\n",
                        t.getType(),
                        t.getCategory().getName(),
                        t.getAmount(),
                        t.getDescription().replace(",", ";"),
                        t.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error exporting CSV: " + e.getMessage());
        }
    }

    public void importWalletFromCSV(User user, String csvFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line = reader.readLine();
            if (line == null || !line.equals("Type,Category,Amount,Description,Date")) {
                throw new RuntimeException("Invalid CSV format.");
            }

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 5) continue;

                try {
                    TransactionType type = TransactionType.valueOf(parts[0].trim());
                    Category category = new Category(parts[1].trim());
                    double amount = Double.parseDouble(parts[2].trim());
                    String description = parts[3].trim().replace(";", ",");
                    LocalDate date = LocalDate.parse(parts[4].trim(), DateTimeFormatter.ISO_LOCAL_DATE);

                    Transaction transaction = new Transaction(type, category, amount, description);
                    transaction.setDate(date);

                    user.getWallet().addTransaction(transaction);
                } catch (Exception ignored) {
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error importing CSV: " + e.getMessage());
        }
    }
}
