package se.yrgo.libraryapp.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import com.radcortez.flyway.test.annotation.H2;

import se.yrgo.libraryapp.entities.LoginInfo;
import se.yrgo.libraryapp.entities.User;
import se.yrgo.libraryapp.entities.UserId;
import java.sql.Connection;

@Tag("integration")
@H2
public class UserDaoIntegrationTest {
    private static DataSource ds;

    @BeforeAll
    static void initDataSource() {
        // this way we do not need to create a new datasource every time
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test");
        UserDaoIntegrationTest.ds = ds;
    }

    @Test
    void getUserById() {
        // this data comes from the test migration files
        final String username = "test";
        final UserId userId = UserId.of(1);
        UserDao userDao = new UserDao(ds);
        Optional<User> maybeUser = userDao.get(Integer.toString(userId.getId()));
        assertThat(maybeUser).isPresent();
        assertThat(maybeUser.get().getName()).isEqualTo(username);
        assertThat(maybeUser.get().getId()).isEqualTo(userId);
    }

    @Test
    void getNonExistingUser() {
        UserDao userDao = new UserDao(ds);

        Optional<User> result = userDao.get("99999");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnTrueIfUsernameIsAvailable() {
        UserDao userDao = new UserDao(ds);

        boolean result = userDao.isNameAvailable("availableUserName123");

        assertThat(result).isTrue();
    }

    @Test
    void shouldRegisterUser() throws SQLException {
        UserDao userDao = new UserDao(ds);

        String username = "newTestUser";
        String realname = "Bosse Test";
        String passwordHash = "hashedPassword123";

        boolean result = userDao.register(username, realname, passwordHash);
        assertThat(result).isTrue();

        Optional<LoginInfo> loginInfo = userDao.getLoginInfo(username);
        assertThat(loginInfo).isPresent();
        assertThat(loginInfo.get().getPasswordHash()).isEqualTo(passwordHash);

        int userId = loginInfo.get().getUserId().getId();

        try (Connection conn = ds.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT role_id FROM user_role WHERE user_id = ?")) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt("role_id")).isEqualTo(2);
            }
        }
    }
}