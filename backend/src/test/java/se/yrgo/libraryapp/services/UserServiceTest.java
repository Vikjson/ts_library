package se.yrgo.libraryapp.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import se.yrgo.libraryapp.dao.UserDao;
import se.yrgo.libraryapp.entities.LoginInfo;
import se.yrgo.libraryapp.entities.UserId;
import org.springframework.security.crypto.password.PasswordEncoder;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class UserServiceTest {
    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder encoder;

    @Test
    @SuppressWarnings("deprecation")
    void correctLogin() {
        final String userId = "1";
        final UserId id = UserId.of(userId);
        final String username = "testuser";
        final String password = "password";
        final String passwordHash = "password";
        final LoginInfo info = new LoginInfo(id, passwordHash);
        final PasswordEncoder encoder =

                org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();

        when(userDao.getLoginInfo(username)).thenReturn(Optional.of(info));

        UserService userService = new UserService(userDao, encoder);
        assertThat(userService.validate(username,
                password)).isEqualTo(Optional.of(id));
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        String username = "testingUser";
        String realname = "Bosse";
        String password = "bredsladd123";
        String passwordHash = "hashedPassword123";

        when(encoder.encode(password)).thenReturn(passwordHash);

        when(userDao.register(username, realname, passwordHash))
                .thenReturn(true);

        UserService userService = new UserService(userDao, encoder);

        boolean hasRegisteredUser = userService.register(username, realname, password);

        assertThat(hasRegisteredUser).isTrue();
    }

}