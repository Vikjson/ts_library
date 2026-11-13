package se.yrgo.libraryapp.dao;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se.yrgo.libraryapp.entities.Role;
import se.yrgo.libraryapp.entities.UserId;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class RoleDaoIntegrationTest {

    private RoleDao roleDao;

    @BeforeEach
    void setUp() throws SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:roletest;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");

        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .locations("classpath:db/migration")
                .cleanDisabled(false)
                .load();

        flyway.clean();
        flyway.migrate();

        roleDao = new RoleDao(ds);

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(
                    "INSERT INTO user (id, user, password_hash) " +
                    "VALUES (999, 'testuser', 'hash')");

            stmt.executeUpdate(
                    "INSERT INTO user_role (user_id, role_id) " +
                    "SELECT 999, id FROM role WHERE role = 'user'");
        }
    }

    @Test
    void shouldReturnRolesForUser() {
        List<Role> result = roleDao.get(UserId.of(999));

        assertThat(result).contains(Role.USER);
    }

    @Test
    void shouldReturnEmptyListForUserWithoutRoles() {
        List<Role> result = roleDao.get(UserId.of(998));

        assertThat(result).isEmpty();
    }
}
