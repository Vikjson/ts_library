package se.yrgo.libraryapp.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import se.yrgo.libraryapp.entities.forms.RegisterUserData;
import se.yrgo.libraryapp.services.UserService;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class RegisterUserControllerTest {

    @Mock
    private UserService userService;

    private RegisterUserController controller;

    @BeforeEach
    void setUp() {
        controller = new RegisterUserController(userService);
    }

    @Test
    void shouldRegisterValidUser() {
        RegisterUserData userData = mock(RegisterUserData.class);

        when(userData.getName()).thenReturn("testuser");
        when(userData.getRealName()).thenReturn("Test User");
        when(userData.getPassword()).thenReturn("password123");

        when(userService.register("testuser", "Test User", "password123"))
                .thenReturn(true);

        boolean result = controller.register(userData);

        assertThat(result).isTrue();

        verify(userService).register("testuser", "Test User", "password123");
    }

    @Test
    void shouldRejectInvalidUsername() {
        RegisterUserData userData = mock(RegisterUserData.class);

        when(userData.getName()).thenReturn("a");

        boolean result = controller.register(userData);

        assertThat(result).isFalse();
        verifyNoInteractions(userService);
    }

    @Test
    void shouldCheckUsernameAvailability() {
        when(userService.isNameAvailable("testuser")).thenReturn(true);

        boolean result = controller.isNameAvailable("testuser");

        assertThat(result).isTrue();
        verify(userService).isNameAvailable("testuser");
    }
}