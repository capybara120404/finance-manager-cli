package com.daniil.financemanager.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class Transaction {
    private final TransactionType type;
    private final Category category;
    private final double amount;
    private final String description;
    private LocalDate date;

    @JsonCreator
    public Transaction(
            @JsonProperty("type") TransactionType type,
            @JsonProperty("category") Category category,
            @JsonProperty("amount") double amount,
            @JsonProperty("description") String description
    ) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = LocalDate.now();
    }

    public TransactionType getType() {
        return type;
    }

    public Category getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
