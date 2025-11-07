package com.daniil.financemanager.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
    private final List<Transaction> transactions;
    private final Map<Category, Budget> budgets;
    @JsonBackReference
    private User user;

    @JsonCreator
    public Wallet(@JsonProperty("user") User user) {
        this.transactions = new ArrayList<>();
        this.budgets = new HashMap<>();
        this.user = user;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public Map<Category, Budget> getBudgets() {
        return budgets;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
