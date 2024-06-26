package com.beko.junit.service;

import com.beko.junit.DTO.User;
import com.beko.junit.Service.UserService;
import com.beko.junit.paramresolver.UserServiceParamResolver;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_METHOD) //By default
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({
        UserServiceParamResolver.class
})
class UserServiceTest {
    private static final User MAKO = User.of(2, "Mako", "456");
    private static final User BEKO = User.of(1, "Beko", "123");
    private UserService userService;

    @BeforeAll
    static void init() {
        System.out.println("Before all: ");
    }
    @BeforeEach
    void prepare(UserService userService) {
        System.out.println("Before each: " + this);
        this.userService = userService;
    }
    @Order(1)
    @DisplayName("users will be empty if no user added")
    @Test
    void userEmptyIfNoUserAdded(UserService userService) {
        System.out.println("Test 1: " + this);
        userService = new UserService();
        var users = userService.getAll();

        //Hamcrest
        MatcherAssert.assertThat(users, IsEmptyCollection.empty());
//        assertTrue(users.isEmpty());
    }

    @Order(2)
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

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    static void closeConnectionPull() {
        System.out.println("After all: ");
    }

    @Nested
    @DisplayName("test user login functionality")
    @Tag("Login")
    class LoginTest {
//        @Tag("login")
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
//        @Tag("login")
        @Test
        void throwExceptionIfUsernameOrPasswordIsNull() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy")),
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
            );
        }

//        @Tag("login")
        @Test
        void loginFailIfPasswordNotCorrect() {
            userService.add(BEKO);
            var maybeUser = userService.login(BEKO.getName(), "jlk");

            assertTrue(maybeUser.isEmpty());
        }

//        @Tag("login")
        @Test
        void loginFailIfUserDoesNotExists() {
            userService.add(BEKO);
            var maybeUser = userService.login("akldf", MAKO.getPassword());

            assertTrue(maybeUser.isEmpty());
        }
    }
}
