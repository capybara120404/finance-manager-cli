package com.daniil.financemanager.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Budget {
    private final Category category;
    private final double limit;
    private double spent;

    @JsonCreator
    public Budget(@JsonProperty("category") Category category, @JsonProperty("limit") double limit) {
        this.category = category;
        this.limit = limit;
        this.spent = 0.0;
    }

    public Category getCategory() {
        return category;
    }

    public double getLimit() {
        return limit;
    }

    public double getSpent() {
        return spent;
    }

    public void addExpense(double amount) {
        this.spent += amount;
    }
}
