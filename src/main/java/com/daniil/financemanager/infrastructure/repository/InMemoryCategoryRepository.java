package com.daniil.financemanager.infrastructure.repository;

import com.daniil.financemanager.domain.model.Category;
import com.daniil.financemanager.domain.repository.CategoryRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryCategoryRepository implements CategoryRepository {
    private final Map<String, Category> categories;

    public InMemoryCategoryRepository() {
        this.categories = new HashMap<>();
    }

    @Override
    public Optional<Category> findByName(String name) {
        return Optional.ofNullable(categories.get(name.toLowerCase()));
    }

    @Override
    public void save(Category category) {
        categories.put(category.getName().toLowerCase(), category);
    }

    @Override
    public void delete(Category category) {
        categories.remove(category.getName().toLowerCase());
    }
}
