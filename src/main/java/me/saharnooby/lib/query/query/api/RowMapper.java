package me.saharnooby.lib.query.query.api;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper<T> {

    T map(ResultSet rs) throws SQLException;

}