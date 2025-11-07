package com.daniil.financemanager.domain.repository;

import com.daniil.financemanager.domain.model.User;
import com.daniil.financemanager.domain.model.Wallet;

import java.util.Optional;

public interface WalletRepository {
    Optional<Wallet> findByUser(User user);

    void save(Wallet wallet);
}
