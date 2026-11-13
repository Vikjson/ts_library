
package se.yrgo.libraryapp.dao;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se.yrgo.libraryapp.entities.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class BookDaoIntegrationTest {

    private BookDao bookDao;

    @BeforeEach
    void setUp() throws SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:booktest;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");

        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .locations("classpath:db/migration")
                .cleanDisabled(false)
                .load();

        flyway.clean();
        flyway.migrate();

        bookDao = new BookDao(ds);

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(
                    "INSERT INTO dewey_decimal_system " +
                    "(code, class) VALUES (999, 'Test Classification')");

            stmt.executeUpdate(
                    "INSERT INTO book_edition " +
                    "(isbn, title, author, dds_code) VALUES " +
                    "('9999999999999', 'Test Book', 'Test Author', 999)");

            stmt.executeUpdate(
                    "INSERT INTO book (book_id, isbn) VALUES " +
                    "(999, '9999999999999')");
        }
    }

    @Test
    void shouldGetExistingBook() {
        Optional<Book> result = bookDao.get(BookId.of(999));

        assertThat(result).isPresent();
        assertThat(result.get().getEdition().getTitle())
                .isEqualTo("Test Book");
    }

    @Test
    void shouldReturnEmptyForUnknownBook() {
        Optional<Book> result = bookDao.get(BookId.of(99999));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenSearchFieldsAreEmpty() {
        Set<BookEdition> result = bookDao.find(null, null, null);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindBookByTitle() {
        Set<BookEdition> result =
                bookDao.find(null, "Test Book", null);

        assertThat(result)
                .extracting(BookEdition::getTitle)
                .contains("Test Book");
    }

    @Test
    void shouldFindBooksByClassification() {
        DdsClassification classification =
                new DdsClassification(999, "Test Classification");

        Set<BookEdition> result =
                bookDao.withClass(classification);

        assertThat(result)
                .extracting(BookEdition::getTitle)
                .contains("Test Book");
    }

    @Test
    void shouldReturnFalseWhenBookIsNotOnLoan() {
        boolean result = bookDao.returnBook(BookId.of(999));

        assertThat(result).isFalse();
    }
}
