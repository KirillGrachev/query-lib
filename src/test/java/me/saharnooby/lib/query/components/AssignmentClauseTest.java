package me.saharnooby.lib.query.components;

import me.saharnooby.lib.query.query.components.AssignmentClause;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class AssignmentClauseTest {

    @Test
    void testAddAssignments() {
        AssignmentClause ac = new AssignmentClause();
        ac.add("name", "John");
        ac.add("age", 30);

        assertEquals(2, ac.size());
        assertEquals(List.of("`name`", "`age`"), ac.getColumns());
        assertEquals(List.of("John", 30), ac.getValues());
    }
}