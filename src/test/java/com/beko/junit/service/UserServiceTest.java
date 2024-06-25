package com.beko.junit.service;

import org.beko.junit.DTO.User;
import org.beko.junit.Service.UserService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.hamcrest.MatcherAssert.*;

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

        //Hamcrest
        MatcherAssert.assertThat(users, IsEmptyCollection.empty());
//        assertTrue(users.isEmpty());
    }

    @Test
    void userSizeIdUserAdded() {
        System.out.println("Test 2: " + this);
        userService = new UserService();
        userService.add(BEKO);
        userService.add(MAKO);

        var users = userService.getAll();

        //AssertJ
        assertThat(users).hasSize(2);
//        assertEquals(2, users.size());
    }

    @Test
    void loginSuccessesIfUserExists() {
        userService.add(BEKO);
        Optional<User> maybeUser = userService.login(BEKO.getName(), BEKO.getPassword());

        //AssertJ
        assertThat(maybeUser).isPresent();
        maybeUser.ifPresent(user -> assertThat(user).isEqualTo(BEKO));
//        assertTrue(maybeUser.isPresent());
//        maybeUser.ifPresent(user -> assertEquals(BEKO, user));
    }

//    (1 variant)
//    @Test
//    void throwExceptionIfUsernameOrPasswordIsNull() {
//        try {
//            userService.login(null, "dummy");
//            fail();
//        } catch (IllegalArgumentException e) {
//            assertTrue(true);
//        }
//    }

//    (2 variant)
    @Test
    void throwExceptionIfUsernameOrPasswordIsNull() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy")),
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
        );
    }

    @Test
    void usersConvertedToById() {
        userService.add(BEKO, MAKO);

        Map<Integer, User> users = userService.getAllConvertedById();

        //Hamcrest
        MatcherAssert.assertThat(users, IsMapContaining.hasKey(BEKO.getId()));

        //AssertJ
        assertAll(
                () -> assertThat(users).containsKeys(BEKO.getId(), MAKO.getId()),
                () -> assertThat(users).containsValues(BEKO, MAKO)
        );
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
