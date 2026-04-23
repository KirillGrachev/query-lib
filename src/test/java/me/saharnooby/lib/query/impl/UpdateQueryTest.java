package me.saharnooby.lib.query.impl;

import me.saharnooby.lib.query.query.impl.UpdateQuery;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class UpdateQueryTest {

    @Test
    void testSimpleUpdate() {
        UpdateQuery query = UpdateQuery.table("users")
                .set("name", "Jane")
                .where("id", 1);

        assertEquals("UPDATE `users` SET `name` = ? WHERE `id` = ?;", query.getSql());
        assertEquals(List.of("Jane", 1), query.getParams());
    }

    @Test
    void testUpdateNoSetException() {
        UpdateQuery query = UpdateQuery.table("users").where("id", 1);
        assertThrows(IllegalStateException.class, query::getSql);
    }
}