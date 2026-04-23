package me.saharnooby.lib.query.query.impl;

import lombok.NonNull;

import me.saharnooby.lib.query.query.api.SqlQuery;
import me.saharnooby.lib.query.query.components.AssignmentClause;
import me.saharnooby.lib.query.query.components.WhereClause;
import me.saharnooby.lib.query.util.SqlUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * INSERT запрос с поддержкой ON DUPLICATE KEY UPDATE
 */
public class InsertQuery implements SqlQuery {

	private final String database;
	private final String table;

	private final AssignmentClause assignments = new AssignmentClause();
	private final WhereClause updateConditions = new WhereClause();

	private boolean ignore;
	private Set<String> excludeFromUpdate = new HashSet<>();
	private boolean hasUpdateClause;

	private InsertQuery(String database,
						String table) {
		this.database = database;
		this.table = table;
	}

	public static InsertQuery into(@NonNull String table) {
		return new InsertQuery(null, table);
	}

	public static InsertQuery into(@NonNull String db,
								   @NonNull String table) {
		return new InsertQuery(db, table);
	}

	public InsertQuery ignore() {
		this.ignore = true;
		return this;
	}

	public InsertQuery set(@NonNull String col,
						   @NonNull Object val) {
		assignments.add(col, val);
		return this;
	}

	/**
	 * Включает ON DUPLICATE KEY UPDATE для всех колонок, кроме указанных.
	 */
	public InsertQuery onDuplicateKeyUpdateExcept(
			@NonNull String @NonNull ... excludeCols
	) {

		this.hasUpdateClause = true;
		for (String c : excludeCols) {
			excludeFromUpdate.add(c.toLowerCase());
		}

		return this;

	}

	@Override
	public String getSql() {

		if (assignments.size() == 0)
			throw new IllegalStateException("No values set");

		StringBuilder sb = new StringBuilder("INSERT ");
		if (ignore) sb.append("IGNORE ");
		sb.append("INTO ");

		if (database != null)
			sb.append(SqlUtils.quote(database)).append(".");
		sb.append(SqlUtils.quote(table));

		List<String> cols = assignments.getColumns();
		sb.append(" (").append(String.join(", ", cols)).append(") VALUES (");
		sb.append(cols.stream().map(c -> "?").collect(Collectors.joining(", ")));
		sb.append(")");

		if (hasUpdateClause) {

			List<String> updates = new ArrayList<>();
			List<Object> updateVals = new ArrayList<>();

			for (int i = 0; i < cols.size(); i++) {

				String colName = cols.get(i).replace("`", "")
						.toLowerCase();

				if (!excludeFromUpdate.contains(colName)) {

					updates.add(cols.get(i) + " = ?");
					updateVals.add(assignments.getValues().get(i));

				}

			}

			if (!updates.isEmpty()) {

				sb.append(" ON DUPLICATE KEY UPDATE ");
				sb.append(String.join(", ", updates));

			}
		}

		sb.append(";");
		return sb.toString();

	}

	@Override
	public List<Object> getParams() {

		List<Object> params = new ArrayList<>(assignments.getValues());

		if (hasUpdateClause) {

			for (int i = 0; i < assignments.getColumns().size(); i++) {

				String colName = assignments.getColumns().get(i).replace("`", "")
						.toLowerCase();

				if (!excludeFromUpdate.contains(colName))
					params.add(assignments.getValues().get(i));

			}
		}

		return params;

	}
}