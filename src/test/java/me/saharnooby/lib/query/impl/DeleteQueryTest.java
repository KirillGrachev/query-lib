package me.saharnooby.lib.query.impl;

import me.saharnooby.lib.query.query.impl.DeleteQuery;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class DeleteQueryTest {

    @Test
    void testSimpleDelete() {
        DeleteQuery query = DeleteQuery.from("users")
                .where("id", 1);

        assertEquals("DELETE FROM `users` WHERE `id` = ?;", query.getSql());
        assertEquals(List.of(1), query.getParams());
    }

    @Test
    void testDeleteWithoutWhere() {
        DeleteQuery query = DeleteQuery.from("users");
        assertEquals("DELETE FROM `users`;", query.getSql());
        assertTrue(query.getParams().isEmpty());
    }
}