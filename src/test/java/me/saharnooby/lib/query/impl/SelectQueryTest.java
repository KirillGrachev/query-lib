package me.saharnooby.lib.query.impl;

import me.saharnooby.lib.query.query.impl.SelectQuery;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class SelectQueryTest {

    @Test
    void testSelectAll() {
        SelectQuery query = SelectQuery.from("users").selectAll();
        assertEquals("SELECT * FROM `users`;", query.getSql());
        assertTrue(query.getParams().isEmpty());
    }

    @Test
    void testSelectFields() {
        SelectQuery query = SelectQuery.from("users")
                .select("id", "name")
                .where("id", 1);

        assertEquals("SELECT `id`, `name` FROM `users` WHERE `id` = ?;", query.getSql());
        assertEquals(List.of(1), query.getParams());
    }

    @Test
    void testSelectWithOrderByLimitOffset() {
        SelectQuery query = SelectQuery.from("users")
                .selectAll()
                .orderBy("created_at", true)
                .limit(10)
                .offset(5);

        assertEquals("SELECT * FROM `users` ORDER BY `created_at` DESC LIMIT 10 OFFSET 5;", query.getSql());
    }

    @Test
    void testSelectWithDatabase() {
        SelectQuery query = SelectQuery.from("mydb", "users").selectAll();
        assertEquals("SELECT * FROM `mydb`.`users`;", query.getSql());
    }

    @Test
    void testSelectNoFieldsException() {
        SelectQuery query = SelectQuery.from("users");
        assertThrows(IllegalStateException.class, query::getSql);
    }

    @Test
    void testSelectMixedFieldsException() {
        SelectQuery query = SelectQuery.from("users").select("id");
        assertThrows(IllegalStateException.class, query::selectAll);
    }
}