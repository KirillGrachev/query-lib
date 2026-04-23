package me.saharnooby.lib.query.query.executor;

import lombok.NonNull;

import me.saharnooby.lib.query.query.api.RowMapper;
import me.saharnooby.lib.query.query.api.SqlQuery;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Статический утилитарный класс для выполнения запросов
 */
public final class QueryExecutor {

    private QueryExecutor() {}

    public static <T> Optional<T> queryOne(@NonNull DataSource ds,
                                           @NonNull SqlQuery query,
                                           @NonNull RowMapper<T> mapper
    ) throws SQLException {

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = prepare(query, conn);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return Optional.ofNullable(mapper.map(rs));
            }

            return Optional.empty();

        }
    }

    public static <T> @NonNull List<T> queryList(@NonNull DataSource ds,
                                                 @NonNull SqlQuery query,
                                                 @NonNull RowMapper<T> mapper
    ) throws SQLException {

        List<T> results = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = prepare(query, conn);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                T item = mapper.map(rs);
                if (item != null) results.add(item);
            }

        }

        return results;

    }

    public static int executeUpdate(@NonNull DataSource ds,
                                    @NonNull SqlQuery query
    ) throws SQLException {

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = prepare(query, conn)) {
            return ps.executeUpdate();
        }
    }

    public static long insertWithKey(@NonNull DataSource ds,
                                     @NonNull SqlQuery query
    ) throws SQLException {

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = query.prepare(conn, Statement.RETURN_GENERATED_KEYS)) {
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }

        }

        throw new SQLException("No generated key returned");

    }

    private static PreparedStatement prepare(@NonNull SqlQuery query,
                                             Connection conn) throws SQLException {
        return query.prepare(conn);
    }
}