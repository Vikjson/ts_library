package se.yrgo.libraryapp.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import se.yrgo.libraryapp.dao.BookDao;
import se.yrgo.libraryapp.entities.BookEdition;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class BookControllerTest {

    @Mock
    private BookDao bookDao;

    private BookController controller;

    @BeforeEach
    void setUp() {
        controller = new BookController(bookDao);
    }

    @Test
    void shouldSearchBooksByTitle() {
        Set<BookEdition> books = Set.of(
                new BookEdition("Test Book", "Test Author", "1234567890123")
        );

        when(bookDao.find(null, "Test Book", null))
                .thenReturn(books);

        Set<BookEdition> result =
                controller.search(null, "Test Book", null, null);

        assertThat(result).containsExactlyInAnyOrderElementsOf(books);

        verify(bookDao).find(null, "Test Book", null);
        verifyNoMoreInteractions(bookDao);
    }

    @Test
    void shouldSearchBooksByClassification() {
        controller.search(null, null, null, "100");

        verify(bookDao).withClass(
                argThat(classification ->
                        classification.getCode() == 100)
        );

        verify(bookDao, never()).find(any(), any(), any());
    }
}