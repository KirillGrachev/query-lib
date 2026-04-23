package me.saharnooby.lib.query.batch;

import lombok.NonNull;
import me.saharnooby.lib.query.query.api.SqlQuery;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Исполнитель пакетных запросов (Batch)
 * Требует, чтобы все запросы в пакете имели одинаковую SQL-структуру
 */
public final class BatchExecutor {

    private final String sqlTemplate;
    private final List<List<Object>> batchParams = new ArrayList<>();

    private BatchExecutor(String sql) {
        this.sqlTemplate = sql;
    }

    public static BatchExecutor of(@NonNull SqlQuery sampleQuery) {
        return new BatchExecutor(sampleQuery.getSql());
    }

    public BatchExecutor add(@NonNull SqlQuery query) {

        if (!query.getSql().equals(this.sqlTemplate)) {
            throw new IllegalArgumentException("Query structure mismatch in batch");
        }

        this.batchParams.add(query.getParams());
        return this;

    }

    public int[] execute(@NonNull DataSource ds) throws SQLException {

        if (batchParams.isEmpty()) return new int[0];

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlTemplate)) {

            for (List<Object> params : batchParams) {

                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }

                ps.addBatch();

            }

            return ps.executeBatch();

        }
    }
}