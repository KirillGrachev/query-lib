package me.saharnooby.lib.query.query.components;

import lombok.NonNull;
import me.saharnooby.lib.query.util.SqlUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Отвечает за формирование блока SET (для UPDATE) или колонок/значений (для INSERT).
 */
public class AssignmentClause {

    private final List<String> columns = new ArrayList<>();
    private final List<Object> values = new ArrayList<>();

    public void add(@NonNull String column, @NonNull Object value) {

        SqlUtils.validateIdentifier(column);

        columns.add(SqlUtils.quote(column));
        values.add(value);

    }

    public List<String> getColumns() {
        return columns;
    }

    public List<Object> getValues() {
        return values;
    }

    public int size() {
        return columns.size();
    }
}