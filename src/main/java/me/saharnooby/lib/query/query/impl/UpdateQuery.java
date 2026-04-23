package me.saharnooby.lib.query.query.impl;

import lombok.NonNull;

import me.saharnooby.lib.query.query.api.SqlQuery;
import me.saharnooby.lib.query.query.components.AssignmentClause;
import me.saharnooby.lib.query.query.components.WhereClause;
import me.saharnooby.lib.query.util.SqlUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UPDATE запрос
 */
public class UpdateQuery implements SqlQuery {

	private final String database;
	private final String table;

	private final AssignmentClause assignments = new AssignmentClause();
	private final WhereClause whereClause = new WhereClause();

	private UpdateQuery(String database,
						String table) {
		this.database = database;
		this.table = table;
	}

	public static UpdateQuery table(@NonNull String table) {
		return new UpdateQuery(null, table);
	}

	public static UpdateQuery table(@NonNull String db,
									@NonNull String table) {
		return new UpdateQuery(db, table);
	}

	public UpdateQuery set(@NonNull String col,
						   @NonNull Object val) {
		assignments.add(col, val);
		return this;
	}

	public UpdateQuery where(@NonNull String col,
							 @NonNull Object val) {
		whereClause.addEquality(col, val);
		return this;
	}

	public UpdateQuery whereRaw(@NonNull String sql,
								Object... params) {
		whereClause.addRaw(sql, params);
		return this;
	}

	public UpdateQuery whereNull(@NonNull String col) {
		whereClause.addIsNull(col);
		return this;
	}

	public UpdateQuery whereNullable(@NonNull String col,
									 Object val) {
		whereClause.addNullable(col, val);
		return this;
	}

	@Override
	public String getSql() {

		if (assignments.size() == 0)
			throw new IllegalStateException("No SET values");

		StringBuilder sb = new StringBuilder("UPDATE ");
		if (database != null) sb.append(SqlUtils.quote(database)).append(".");
		sb.append(SqlUtils.quote(table));

		sb.append(" SET ");
		List<String> sets = assignments.getColumns().stream()
				.map(c -> c + " = ?")
				.collect(Collectors.toList());
		sb.append(String.join(", ", sets));

		String where = whereClause.build();
		if (!where.isEmpty()) sb.append(" ").append(where);

		sb.append(";");
		return sb.toString();

	}

	@Override
	public List<Object> getParams() {

		List<Object> params = new ArrayList<>(assignments.getValues());
		params.addAll(whereClause.getParams());

		return params;

	}
}