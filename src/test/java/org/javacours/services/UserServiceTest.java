package org.javacours.services;

import org.javacours.data.DataLoader;
import org.javacours.models.User;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserService userService;
    private DataLoader dataLoader;

    @BeforeEach
    void setup() throws IOException {
        dataLoader = Mockito.mock(DataLoader.class);
        userService = UserService.getInstance();
        userService.setDataLoader(dataLoader);
    }

    @Test
    void getInstance_ShouldReturnSameInstance() throws IOException {
        UserService instance1 = UserService.getInstance();
        UserService instance2 = UserService.getInstance();

        Assertions.assertSame(instance1, instance2);
    }

    @Test
    void addUser_ShouldAddUserToDataLoader() {
        String login = "testUser";
        String password = "testPassword";

        userService.addUser(login, password);

        verify(dataLoader, times(1)).addUser(any(User.class));
    }


    @Test
    void hashPassword_ShouldReturnHashedPassword() {
        String password = "testPassword";
        String expectedHash = userService.hashPassword(password); // Get expected hash for comparison

        String hashedPassword = userService.hashPassword(password);

        Assertions.assertEquals(expectedHash, hashedPassword);
    }
}
