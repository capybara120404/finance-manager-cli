package com.daniil.financemanager.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.UUID;

public class User {
    private final String login;
    private final String password;
    private final String salt;
    @JsonManagedReference
    private final Wallet wallet;

    public User(String login, String password) {
        this.login = login;
        this.salt = generateSalt();
        this.password = hashPassword(password, salt);
        this.wallet = new Wallet(this);
    }

    private String generateSalt() {
        return UUID.randomUUID().toString();
    }

    private String hashPassword(String password, String salt) {
        return Integer.toHexString((password + salt).hashCode());
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public Wallet getWallet() {
        return wallet;
    }
}
