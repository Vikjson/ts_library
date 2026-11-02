package se.yrgo.libraryapp.validators;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class RealNameTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "Frodo Baggins",
            "Sam",
            "Shadow-Fax",
            "S4rum4n the White",
            "ARAGORN KOMMER"
    })
    void shouldAcceptValidNames(String name) {
        assertThat(RealName.validate(name)).isTrue();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/bad_words.txt")
    void shouldRejectNamesWithBadWords(String name) {
        assertThat(RealName.validate(name)).isFalse();
    }
}
