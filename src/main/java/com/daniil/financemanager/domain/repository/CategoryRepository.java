package com.daniil.financemanager.domain.repository;

import com.daniil.financemanager.domain.model.Category;

import java.util.Optional;

public interface CategoryRepository {
    Optional<Category> findByName(String name);

    void save(Category category);

    void delete(Category category);
}
