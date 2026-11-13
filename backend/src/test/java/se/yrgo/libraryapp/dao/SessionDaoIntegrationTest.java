package se.yrgo.libraryapp.dao;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.CredentialsException;

import se.yrgo.libraryapp.entities.UserId;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class SessionDaoIntegrationTest {

    private SessionDao sessionDao;

    @BeforeEach
    void setUp() throws SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:sessiontest;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");

        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .locations("classpath:db/migration")
                .cleanDisabled(false)
                .load();

        flyway.clean();
        flyway.migrate();

        sessionDao = new SessionDao(ds);

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(
                    "INSERT INTO user (id, user, password_hash) " +
                    "VALUES (999, 'testuser', 'hash')");
        }
    }

    @Test
    void shouldCreateAndValidateSession() {
        UserId userId = UserId.of(999);

        UUID sessionId = sessionDao.create(userId);
        UserId result = sessionDao.validate(sessionId);

        assertThat(sessionId).isNotNull();
        assertThat(result).isEqualTo(userId);
    }

    @Test
    void shouldDeleteSession() {
        UserId userId = UserId.of(999);
        UUID sessionId = sessionDao.create(userId);

        sessionDao.delete(sessionId);

        assertThatThrownBy(() -> sessionDao.validate(sessionId))
                .isInstanceOf(CredentialsException.class);
    }

    @Test
    void shouldRejectUnknownSession() {
        UUID unknownSession = UUID.randomUUID();

        assertThatThrownBy(() -> sessionDao.validate(unknownSession))
                .isInstanceOf(CredentialsException.class);
    }
}