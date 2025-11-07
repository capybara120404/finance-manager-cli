package com.daniil.financemanager.domain.service;

import com.daniil.financemanager.domain.model.User;
import com.daniil.financemanager.domain.repository.UserRepository;
import com.daniil.financemanager.domain.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private WalletRepository walletRepository;
    private UserRepository userRepository;
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        walletRepository = mock(WalletRepository.class);
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository, walletRepository);
        user = new User("capybara120404", "qwerty");
    }

    @Test
    void testSignUpSuccess() {
        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.empty());

        userService.signUp(user.getLogin(), "qwerty");

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testSignUpDuplicateLogin() {
        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.signUp(user.getLogin(), "qwerty"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSignInSuccess() {
        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));

        boolean result = userService.signIn(user.getLogin(), "qwerty");

        assertTrue(result);
    }

    @Test
    void testSignInWrongPassword() {
        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));

        boolean result = userService.signIn(user.getLogin(), "wrongpassword");

        assertFalse(result);
    }

    @Test
    void testSignInUserNotFound() {
        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.empty());

        boolean result = userService.signIn(user.getLogin(), "qwerty");

        assertFalse(result);
    }

    @Test
    void testFindUserByLogin() {
        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));

        User found = userService.findUserByLogin(user.getLogin());

        assertNotNull(found);
        assertEquals(user.getLogin(), found.getLogin());
    }
}
