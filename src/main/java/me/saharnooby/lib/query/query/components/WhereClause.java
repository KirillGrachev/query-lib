package me.saharnooby.lib.query.query.components;

import lombok.Getter;
import lombok.NonNull;
import me.saharnooby.lib.query.util.SqlUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WhereClause {

    private final List<String> conditions = new ArrayList<>();
    @Getter private final List<Object> params = new ArrayList<>();

    public void addEquality(@NonNull String column,
                            @NonNull Object value) {

        SqlUtils.validateIdentifier(column);

        conditions.add(SqlUtils.quote(column) + " = ?");
        params.add(value);

    }

    public void addRaw(@NonNull String sqlFragment,
                       Object @NonNull ... args) {
        conditions.add("(" + sqlFragment + ")");
        Collections.addAll(params, args);
    }

    public void addIsNull(@NonNull String column) {
        SqlUtils.validateIdentifier(column);
        conditions.add(SqlUtils.quote(column) + " IS NULL");
    }

    public void addNullable(@NonNull String column, Object value) {
        if (value == null) addIsNull(column);
        else {
            addEquality(column, value);
        }
    }

    public boolean isEmpty() {
        return conditions.isEmpty();
    }

    public String build() {
        if (isEmpty()) return "";
        return "WHERE " + String.join(" AND ",
                conditions);
    }
}