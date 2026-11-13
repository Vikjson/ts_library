package se.yrgo.libraryapp.controllers;

import io.jooby.Context;
import io.jooby.StatusCode;
import io.jooby.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import se.yrgo.libraryapp.dao.RoleDao;
import se.yrgo.libraryapp.dao.SessionDao;
import se.yrgo.libraryapp.entities.forms.LoginData;
import se.yrgo.libraryapp.services.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import se.yrgo.libraryapp.entities.*;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class LoginControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private RoleDao roleDao;

    @Mock
    private SessionDao sessionDao;

    @Mock
    private Context context;

    private LoginController controller;

    @BeforeEach
    void setUp() {
        controller = new LoginController(
                userService,
                roleDao,
                sessionDao);
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginData loginData = new LoginData("testuser", "password");
        UserId userId = UserId.of("1");
        UUID sessionId = UUID.randomUUID();

        List<Role> roles = List.of(Role.USER);

        when(userService.validate("testuser", "password"))
                .thenReturn(Optional.of(userId));

        when(sessionDao.create(userId))
                .thenReturn(sessionId);

        when(roleDao.get(userId))
                .thenReturn(roles);

        List<Role> result = controller.login(context, null, loginData);

        assertThat(result).containsExactly(Role.USER);

        verify(userService).validate("testuser", "password");
        verify(sessionDao).create(userId);
        verify(roleDao).get(userId);
        verify(context).setResponseCookie(any(Cookie.class));
        verify(context, never()).setResponseCode(StatusCode.UNAUTHORIZED);
    }

    @Test
    void shouldRejectInvalidLogin() {
        LoginData loginData = new LoginData("testuser", "wrongpassword");

        when(userService.validate("testuser", "wrongpassword"))
                .thenReturn(Optional.empty());

        List<Role> result = controller.login(context, null, loginData);

        assertThat(result).isEmpty();

        verify(context).setResponseCode(StatusCode.UNAUTHORIZED);
        verify(sessionDao, never()).create(any());
        verifyNoInteractions(roleDao);
    }

    @Test
    void shouldNotLoginWhenAlreadyLoggedIn() {
        UUID sessionId = UUID.randomUUID();
        UserId userId = UserId.of("1");

        when(sessionDao.validate(sessionId))
                .thenReturn(userId);

        List<Role> result = controller.login(
                context,
                sessionId.toString(),
                null);

        assertThat(result).isEmpty();

        verifyNoInteractions(userService, roleDao);
        verify(sessionDao, never()).create(any());
        verifyNoInteractions(context);
    }

    @Test
    void shouldReturnRolesForValidSession() {
        UUID sessionId = UUID.randomUUID();
        UserId userId = UserId.of("1");
        List<Role> roles = List.of(Role.USER);

        when(sessionDao.validate(sessionId))
                .thenReturn(userId);

        when(roleDao.get(userId))
                .thenReturn(roles);

        List<Role> result = controller.isLoggedIn(sessionId.toString());

        assertThat(result).containsExactly(Role.USER);

        verify(sessionDao).validate(sessionId);
        verify(roleDao).get(userId);
        verifyNoInteractions(userService);
    }
}