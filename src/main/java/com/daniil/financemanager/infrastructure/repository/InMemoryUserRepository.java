package com.daniil.financemanager.infrastructure.repository;

import com.daniil.financemanager.domain.model.User;
import com.daniil.financemanager.domain.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users;

    public InMemoryUserRepository() {
        this.users = new HashMap<>();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return Optional.ofNullable(users.get(login));
    }

    @Override
    public void save(User user) {
        users.put(user.getLogin(), user);
    }
}
