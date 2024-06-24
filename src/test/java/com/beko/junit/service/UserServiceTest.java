package com.beko.junit.service;

import org.beko.junit.DTO.User;
import org.beko.junit.Service.UserService;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_METHOD) //By default
class UserServiceTest {
    private static final User MAKO = User.of(2, "Mako", "456");
    private static final User BEKO = User.of(1, "Beko", "123");
    private UserService userService;

    @BeforeAll
    static void init() {
        System.out.println("Before all: ");
    }
    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
        userService = new UserService();
    }
    @Test
    void userEmptyIfNoUserAdded() {
        System.out.println("Test 1: " + this);
        userService = new UserService();
        var users = userService.getAll();
        assertTrue(users.isEmpty());
    }

    @Test
    void userSizeIdUserAdded() {
        System.out.println("Test 2: " + this);
        userService = new UserService();
        userService.add(BEKO);
        userService.add(MAKO);

        var users = userService.getAll();
        assertEquals(2, users.size());
    }

    @Test
    void loginSuccessesIfUserExists() {
        userService.add(BEKO);
        Optional<User> maybeUser = userService.login(BEKO.getName(), BEKO.getPassword());

        assertTrue(maybeUser.isPresent());
        maybeUser.ifPresent(user -> assertEquals(BEKO, user));
    }

    @Test
    void loginFailIfPasswordNotCorrect() {
        userService.add(BEKO);
        var maybeUser = userService.login(BEKO.getName(), "jlk");

        assertTrue(maybeUser.isEmpty());
    }

    @Test
    void loginFailIfUserDoesNotExists() {
        userService.add(BEKO);
        var maybeUser = userService.login("akldf", MAKO.getPassword());

        assertTrue(maybeUser.isEmpty());
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    static void closeConnectionPull() {
        System.out.println("After all: ");
    }
}
