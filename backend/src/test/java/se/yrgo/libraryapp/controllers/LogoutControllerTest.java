package se.yrgo.libraryapp.controllers;

import io.jooby.Context;
import io.jooby.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import se.yrgo.libraryapp.dao.SessionDao;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class LogoutControllerTest {

    @Mock
    private SessionDao sessionDao;

    @Mock
    private Context context;

    private LogoutController controller;

    @BeforeEach
    void setUp() {
        controller = new LogoutController(sessionDao);
    }

    @Test
    void shouldDeleteSessionAndClearCookie() {
        UUID sessionId = UUID.randomUUID();

        controller.logout(context, sessionId.toString());

        verify(sessionDao).delete(sessionId);
        verify(context).setResponseCookie(argThat(
                cookie -> cookie.getName().equals("session")
                        && cookie.getValue().isEmpty()
        ));
    }

    @Test
    void shouldDoNothingWhenCookieIsMissing() {
        controller.logout(context, null);

        verifyNoInteractions(sessionDao, context);
    }

    @Test
    void shouldClearCookieWhenSessionIdIsInvalid() {
        controller.logout(context, "invalid-uuid");

        verifyNoInteractions(sessionDao);
        verify(context).setResponseCookie(any(Cookie.class));
    }
}