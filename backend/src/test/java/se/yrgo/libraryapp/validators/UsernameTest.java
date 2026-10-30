package se.yrgo.libraryapp.validators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class UsernameTest {
    @ParameterizedTest
    @ValueSource(strings = { "jossan34", "anna", "B0ss3", "user_name", "user.name", "user-name", "l@ban", "1234" })
    void shouldAcceptNamesFourOrLonger(String username) {
        assertThat(Username.validate(username)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = { "1", "fi", "57t", "ada", "999" })
    void shoulrRejectNamesShorterThanFour(String username) {
        assertThat(Username.validate(username)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "abcdefghijklmnopqrstuvwxyz",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "0123456789",
            "@._-",
            "abc123XYZ@._-",
            "aaaa",
            "0000",
            "@@@@",
            "....",
            "____",
            "----"
    })
    void shouldAcceptAllValidCharacters(String username) {
        assertThat(Username.validate(username)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "anna35!",
            "user#name",
            "user$name",
            "user%name",
            "user&name",
            "user*name",
            "user+name",
            "user=name",
            "user/name",
            "user\\name",
            "user:name",
            "user;name",
            "user?name",
            "user,name",
            "user(name)",
            "user[name]",
            "user{name}",
            "user name",
            " user",
            "user ",
            "user\tname",
            "user\nname",
            "jösse",
            "åsa123",
            "äntligen",
            "café",
            "你好世界",
            "user😊"
    })
    void shouldRejectInvalidCharacters(String username) {
        assertThat(Username.validate(username)).isFalse();
    }

    @Test
    void shouldAcceptLongUsernameUpToSeventy() {
        String username = "a".repeat(70);
        String username2 = "b".repeat(69);
        assertThat(Username.validate(username)).isTrue();
        assertThat(Username.validate( username2)).isTrue();
    }

    
    @Test
    void shouldRejectLongUsernameOverSeventy() {
        String username = "a".repeat(71);
        assertThat(Username.validate(username)).isFalse();
    }

    @ParameterizedTest
    @EmptySource
    void shouldRejectEmptyUsername(String username) {
        assertThat(Username.validate(username)).isFalse();
    }
}
