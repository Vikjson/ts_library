package se.yrgo.libraryapp.dao;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.yrgo.libraryapp.entities.DdsClassification;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ClassificationDaoIntegrationTest {

    private ClassificationDao classificationDao;

    @BeforeEach
    void setUp() throws SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:classificationtest;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");

        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .locations("classpath:db/migration")
                .cleanDisabled(false)
                .load();

        flyway.clean();
        flyway.migrate();

        classificationDao = new ClassificationDao(ds);

        try (Connection conn = ds.getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(
                    "INSERT INTO dewey_decimal_system VALUES (100, 'Philosophy')");

            stmt.executeUpdate(
                    "INSERT INTO dewey_decimal_system VALUES (200, 'Religion')");

            stmt.executeUpdate(
                    "INSERT INTO book_edition VALUES " +
                            "('1234567890123', 'Test Book', 'Test Author', 100)");

            stmt.executeUpdate(
                    "INSERT INTO book_edition VALUES " +
                            "('1234567890124', 'Another Book', 'Test Author', 100)");
        }
    }

    @Test
    void shouldReturnAvailableClassifications() {
        List<DdsClassification> result = classificationDao.getAvailable();

        assertThat(result)
                .extracting(DdsClassification::getCode)
                .contains(100)
                .doesNotHaveDuplicates();
    }

}