package se.yrgo.libraryapp.validators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class UtilsTest {

    @ParameterizedTest
    @ValueSource(strings = { "hej ", "Kalle Johnsson", " Beata\tKarotena ", " Dag   Nattson",
            "Sigrida Baronessa Lagfartsson\naf Grumigt \t\rvatten", "        " })
    void shouldKeepAnyWhitespaces(String name) {
        assertThat(Utils.cleanAndUnLeet(name)).isEqualTo(name.toLowerCase());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Gr3g0ry; Tr3xx",
            "S4g01in4:63r7i1550n",
            "R@gn-(/hild<>?)._",
            "[J0h@n!#$%&*]",
            "M4r!a+={}|~^",
            "P3t3r\tJ0hns0n",
            "ÄNN4 ÖB3RG~ ^" })
    void shouldOnlyContainAlphabeticalLettersAndWhiteSpaces(String name) {
        assertThat(Utils.cleanAndUnLeet(name)).matches("[a-zåäö\\s]+");
    }

    @ParameterizedTest
    @ValueSource(strings = { "!#$%&*", "<>._-", "+={}[]|~^" })
    void shouldRemoveSpecialCharacters(String name) {
        assertThat(Utils.cleanAndUnLeet(name)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = { "VIXIVIX", "FOXY PROXY", "ÅSA GÅS FIKAR ALLTID" })
    void shouldConvertLettersToLowerCase(String name) {
        assertThat(Utils.cleanAndUnLeet(name)).isEqualTo(name.toLowerCase());
    }

    @ParameterizedTest
    @CsvSource({
            "h3j, hej",
            "Vik70ri4, viktoria",
            "83rti1 J0hn550n, bertil johnsson",
            "P3tr4, petra",
            "1, l",
            "3, e",
            "4, a",
            "5, s",
            "6, b",
            "7, t",
            "8, b",
            "0, o"
    })
    void shouldConvertLeetToLetters(String leetSpeakInput, String expectedConversion) {
        assertThat(Utils.cleanAndUnLeet(leetSpeakInput)).isEqualTo(expectedConversion);
    }

    @Test
    void shouldHandleEmptyString() {
        assertThat(Utils.cleanAndUnLeet("")).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "H3LL0, hll",
            "Anna-Maria, annamaria",
            "'Korvspad 123', 'korvspad '",
            "ÅSA ÄR GÅS, åsa är gås"
    })
    void shouldRemoveNonLetterCharacters(
            String inputName, String expectedConversion) {
        assertThat(Utils.onlyLettersAndWhitespace(inputName)).isEqualTo(expectedConversion);
    }
}
