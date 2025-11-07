package com.daniil.financemanager.domain.service;

import com.daniil.financemanager.domain.model.User;
import com.daniil.financemanager.domain.repository.UserRepository;
import com.daniil.financemanager.domain.repository.WalletRepository;

public class UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public UserService(UserRepository userRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    public void signUp(String login, String password) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("User with this login already exists");
        }

        User user = new User(login, password);
        userRepository.save(user);
        walletRepository.save(user.getWallet());
    }

    public boolean signIn(String login, String password) {
        return userRepository.findByLogin(login)
                .map(user -> {
                    String hashedInputPassword = Integer.toHexString((password + user.getSalt()).hashCode());
                    return user.getPassword().equals(hashedInputPassword);
                })
                .orElse(false);
    }

    public User findUserByLogin(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }
}
