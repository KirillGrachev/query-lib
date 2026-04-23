package me.saharnooby.lib.query.components;

import me.saharnooby.lib.query.query.components.WhereClause;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class WhereClauseTest {

    @Test
    void testEmptyWhere() {
        WhereClause wc = new WhereClause();
        assertTrue(wc.isEmpty());
        assertEquals("", wc.build());
        assertTrue(wc.getParams().isEmpty());
    }

    @Test
    void testAddEquality() {
        WhereClause wc = new WhereClause();
        wc.addEquality("id", 1);
        wc.addEquality("status", "ACTIVE");

        assertFalse(wc.isEmpty());
        assertEquals("WHERE `id` = ? AND `status` = ?", wc.build());
        assertEquals(List.of(1, "ACTIVE"), wc.getParams());
    }

    @Test
    void testAddIsNull() {
        WhereClause wc = new WhereClause();
        wc.addIsNull("deleted_at");

        assertEquals("WHERE `deleted_at` IS NULL", wc.build());
        assertTrue(wc.getParams().isEmpty());
    }

    @Test
    void testAddNullableNull() {
        WhereClause wc = new WhereClause();
        wc.addNullable("name", null);

        assertEquals("WHERE `name` IS NULL", wc.build());
    }

    @Test
    void testAddNullableValue() {
        WhereClause wc = new WhereClause();
        wc.addNullable("name", "John");

        assertEquals("WHERE `name` = ?", wc.build());
        assertEquals(List.of("John"), wc.getParams());
    }

    @Test
    void testAddRaw() {
        WhereClause wc = new WhereClause();
        wc.addEquality("id", 1);
        wc.addRaw("age > ? OR age < ?", 10, 5);

        assertEquals("WHERE `id` = ? AND (age > ? OR age < ?)", wc.build());
        assertEquals(List.of(1, 10, 5), wc.getParams());
    }
}