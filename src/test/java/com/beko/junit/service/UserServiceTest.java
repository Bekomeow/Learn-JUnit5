package com.beko.junit.service;

import org.beko.junit.DTO.User;
import org.beko.junit.Service.UserService;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_METHOD) //By default
class UserServiceTest {
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
        userService.add(new User());
        userService.add(new User());

        var users = userService.getAll();
        assertEquals(2, users.size());
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
