package com.daniil.financemanager.domain.repository;

import com.daniil.financemanager.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByLogin(String login);

    void save(User user);
}
