package me.saharnooby.lib.query.impl;

import me.saharnooby.lib.query.query.impl.InsertQuery;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class InsertQueryTest {

    @Test
    void testSimpleInsert() {
        InsertQuery query = InsertQuery.into("users")
                .set("name", "John")
                .set("age", 30);

        assertEquals("INSERT INTO `users` (`name`, `age`) VALUES (?, ?);", query.getSql());
        assertEquals(List.of("John", 30), query.getParams());
    }

    @Test
    void testInsertIgnore() {
        InsertQuery query = InsertQuery.into("users")
                .ignore()
                .set("name", "John");

        assertEquals("INSERT IGNORE INTO `users` (`name`) VALUES (?);", query.getSql());
    }

    @Test
    void testInsertOnDuplicateKeyUpdateExcept() {
        InsertQuery query = InsertQuery.into("users")
                .set("id", 1)
                .set("name", "John")
                .set("updated_at", "NOW()")
                .onDuplicateKeyUpdateExcept("id");

        String sql = query.getSql();
        assertTrue(sql.contains("ON DUPLICATE KEY UPDATE"));
        assertTrue(sql.contains("`name` = ?"));
        assertTrue(sql.contains("`updated_at` = ?"));
        assertFalse(sql.contains("`id` = ?"));

        // Параметры: сначала values (id, name, updated_at), потом update values (name, updated_at)
        List<Object> params = query.getParams();
        assertEquals(5, params.size());
        assertEquals(1, params.get(0)); // id value
        assertEquals("John", params.get(1)); // name value
        assertEquals("NOW()", params.get(2)); // updated_at value
        assertEquals("John", params.get(3)); // name update
        assertEquals("NOW()", params.get(4)); // updated_at update
    }

    @Test
    void testInsertNoValuesException() {
        InsertQuery query = InsertQuery.into("users");
        assertThrows(IllegalStateException.class, query::getSql);
    }
}