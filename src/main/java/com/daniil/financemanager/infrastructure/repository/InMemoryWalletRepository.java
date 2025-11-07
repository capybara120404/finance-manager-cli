package com.daniil.financemanager.infrastructure.repository;

import com.daniil.financemanager.domain.model.User;
import com.daniil.financemanager.domain.model.Wallet;
import com.daniil.financemanager.domain.repository.WalletRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryWalletRepository implements WalletRepository {
    private final Map<String, Wallet> wallets;

    public InMemoryWalletRepository() {
        this.wallets = new HashMap<>();
    }

    @Override
    public Optional<Wallet> findByUser(User user) {
        return Optional.ofNullable(wallets.get(user.getLogin()));
    }

    @Override
    public void save(Wallet wallet) {
        wallets.put(wallet.getUser().getLogin(), wallet);
    }
}
