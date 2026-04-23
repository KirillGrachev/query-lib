package me.saharnooby.lib.query.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SqlUtilsTest {

    @Test
    void testValidateIdentifierValid() {
        assertDoesNotThrow(() -> SqlUtils.validateIdentifier("users"));
        assertDoesNotThrow(() -> SqlUtils.validateIdentifier("user_id_123"));
        assertDoesNotThrow(() -> SqlUtils.validateIdentifier("A"));
    }

    @Test
    void testValidateIdentifierInvalid() {
        assertThrows(IllegalArgumentException.class, () -> SqlUtils.validateIdentifier(""));
        assertThrows(IllegalArgumentException.class, () -> SqlUtils.validateIdentifier("user name"));
        assertThrows(IllegalArgumentException.class, () -> SqlUtils.validateIdentifier("user-name"));
        assertThrows(IllegalArgumentException.class, () -> SqlUtils.validateIdentifier("user.name"));
        assertThrows(IllegalArgumentException.class, () -> SqlUtils.validateIdentifier("SELECT"));
        assertThrows(IllegalArgumentException.class, () -> SqlUtils.validateIdentifier("table@name"));
    }

    @Test
    void testQuote() {
        assertEquals("`users`", SqlUtils.quote("users"));
        assertEquals("`id`", SqlUtils.quote("id"));
    }
}