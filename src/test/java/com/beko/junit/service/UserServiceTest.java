package com.beko.junit.service;

import com.beko.junit.DTO.User;
import com.beko.junit.TestBase;
import com.beko.junit.dao.UserDao;
import com.beko.junit.extension.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.internal.util.MockUtil;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("Fast")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_METHOD) //By default
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({
        UserServiceParamResolver.class,
        PostProcessingExtension.class,
        ConditionalExecution.class,
        MockitoExtension.class,
//        ThrowableExtension.class,
//        GlobalExtension.class
})
class UserServiceTest extends TestBase {
    private static final User MAKO = User.of(2, "Mako", "456");
    private static final User BEKO = User.of(1, "Beko", "123");
    @Captor
    private ArgumentCaptor<Integer> argumentCaptor;
//    @Mock(lenient = true)
    @Mock()
    private UserDao userDao;
    @InjectMocks
    private UserService userService;

    @BeforeAll
    static void init() {
        System.out.println("Before all: ");
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
//        Mockito.lenient().when(userDao.delete(BEKO.getId())).thenReturn(true);
        doReturn(true).when(userDao).delete(BEKO.getId());

//        this.userDao = Mockito.mock(UserDao.class);
//        this.userDao = Mockito.spy(UserDao.class);
//        this.userService = new UserService(userDao);
    }

    @Test
    public void throwExceptionIfDatabaseNotAvailable() {
        Mockito.doThrow(RuntimeException.class).when(userDao).delete(BEKO.getId());

        assertThrows(RuntimeException.class, () -> userService.delete(BEKO.getId()));
    }

    @Test
    public void shouldDeleteExistedUser() {
        userService.add(BEKO);
//        Mockito.doReturn(true).when(userDao).delete(BEKO.getId());
//        Mockito.doReturn(true).when(userDao).delete(Mockito.any());

//        Mockito.when(userDao.delete(BEKO.getId()))
//                .thenReturn(true)
//                .thenReturn(false);

        var deleteResult = userService.delete(BEKO.getId());
        System.out.println(userService.delete(BEKO.getId()));
        System.out.println(userService.delete(BEKO.getId()));

        verify(userDao, Mockito.atLeast(3)).delete(BEKO.getId());
        verify(userDao, Mockito.times(3)).delete(BEKO.getId());

//        var argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(userDao, Mockito.times(3)).delete(argumentCaptor.capture());

//        Mockito.reset();

        assertThat(deleteResult).isTrue();
    }

    @Order(1)
    @DisplayName("users will be empty if no user added")
    @Test
    void userEmptyIfNoUserAdded(UserService userService) throws IOException {
        if (true) {
//            throw new IOException();
            throw new RuntimeException();
        }
        System.out.println("Test 1: " + this);
        userService = new UserService(new UserDao());
        var users = userService.getAll();

        //Hamcrest
        MatcherAssert.assertThat(users, IsEmptyCollection.empty());
//        assertTrue(users.isEmpty());
    }

    @Order(2)
    @Test
    void userSizeIdUserAdded() {
        System.out.println("Test 2: " + this);
        userService = new UserService(new UserDao());
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
    @Timeout(value = 200, unit = TimeUnit.MICROSECONDS)
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
        @Disabled("flaky, need to see")
        void loginFailIfPasswordIsNotCorrect() {
            userService.add(BEKO);
            var maybeUser = userService.login(BEKO.getName(), "jlk");

            assertTrue(maybeUser.isEmpty());
        }

//        @Tag("login")
        @Test
        @RepeatedTest(value = 5, name = RepeatedTest.LONG_DISPLAY_NAME)
        void loginFailIfUserDoesNotExists() {
            userService.add(BEKO);
            var maybeUser = userService.login("dummy", MAKO.getPassword());

            assertTrue(maybeUser.isEmpty());
        }

        @Test
        void checkLoginFunctionalityPerformance() {
            var result = assertTimeout(Duration.ofMillis(200L), () -> {
                Thread.sleep(300L);
                return userService.login("dummy", MAKO.getPassword());
            });

//            var result = assertTimeoutPreemptively(Duration.ofMillis(200L), () -> {
//                Thread.sleep(300L);
//                return userService.login("dummy", MAKO.getPassword());
//            });
        }

        @ParameterizedTest(name = "{arguments} test")
//        @ArgumentsSource()
//        @NullSource
//        @EmptySource
//        @ValueSource(strings = {
//                "Beko", "Mako"
//        })
//        @EnumSource
//        @CsvFileSource(resources = {"/login-test-data.csv"}, delimiter = ',', numLinesToSkip = 1)
//        @CsvSource(value = {
//                "NAME,PASSWORD",
//                "Beko,123",
//                "Mako,456"
//        }, delimiter = ',')
        @MethodSource("com.beko.junit.service.UserServiceTest#getArgumentsForLoginTest")
        void loginParameterizedTest(String name, String password, Optional<User> user) {
            userService.add(BEKO, MAKO);

            var maybeUser = userService.login(name, password);

            System.out.println(maybeUser);

            assertThat(maybeUser).isEqualTo(user);
        }
    }
    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Beko", "123", Optional.of(BEKO)),
                Arguments.of("Mako", "456", Optional.of(MAKO)),
                Arguments.of("dummy", "456", Optional.empty()),
                Arguments.of("Beko", "dummy", Optional.empty())
        );
    }
}

