package me.saharnooby.lib.query.impl;

import me.saharnooby.lib.query.query.impl.CreateTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateTableTest {

    @Test
    void testSimpleCreateTable() {

        CreateTable query = CreateTable.table("users")
                .intKey("id")
                .endColumn()
                .varchar("name", 255)
                .endColumn()
                .bool("is_active")
                .endColumn();

        String sql = query.getSql();

        assertTrue(sql.startsWith("CREATE TABLE `users` ("));
        assertTrue(sql.contains("`id` INT PRIMARY KEY AUTO_INCREMENT NOT NULL"));
        assertTrue(sql.contains("`name` VARCHAR(255)"));
        assertTrue(sql.contains("`is_active` TINYINT(1)"));
        assertTrue(sql.endsWith(");"));
    }

    @Test
    void testCreateTableIfNotExists() {
        CreateTable query = CreateTable.table("users")
                .ifNotExists()
                .intKey("id")
                .endColumn();

        assertTrue(query.getSql().contains("IF NOT EXISTS"));
    }

    @Test
    void testCreateTableWithDefault() {
        CreateTable query = CreateTable.table("users")
                .intKey("id")
                .endColumn()
                .addColumn("status", "VARCHAR(10)")
                .defaultValue("NEW")
                .endColumn();

        String sql = query.getSql();
        assertTrue(sql.contains("`status` VARCHAR(10) DEFAULT 'NEW'"));
    }

    @Test
    void testCreateTableNoColumnsException() {
        CreateTable query = CreateTable.table("empty_table");
        assertThrows(IllegalStateException.class, query::getSql);
    }

    @Test
    void testCreateTableWithDatabase() {
        CreateTable query = CreateTable.table("my_db", "users")
                .intKey("id")
                .endColumn()
                .varchar("email", 100)
                .endColumn();

        String sql = query.getSql();
        assertTrue(sql.contains("CREATE TABLE `my_db`.`users`"));
    }
}