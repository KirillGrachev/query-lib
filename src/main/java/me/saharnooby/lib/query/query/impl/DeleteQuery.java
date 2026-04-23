package me.saharnooby.lib.query.query.impl;

import lombok.NonNull;

import me.saharnooby.lib.query.query.api.SqlQuery;
import me.saharnooby.lib.query.query.components.WhereClause;
import me.saharnooby.lib.query.util.SqlUtils;
import java.util.List;

/**
 * DELETE запрос
 */
public class DeleteQuery implements SqlQuery {

	private final String database;
	private final String table;

	private final WhereClause whereClause = new WhereClause();

	public DeleteQuery(String database,
                       String table) {
		this.database = database;
		this.table = table;
	}

	public static DeleteQuery from(@NonNull String table) {
		return new DeleteQuery(null, table);
	}

	public static DeleteQuery from(@NonNull String db,
								   @NonNull String table) {
		return new DeleteQuery(db, table);
	}

	public DeleteQuery where(@NonNull String col,
							 @NonNull Object val) {
		whereClause.addEquality(col, val);
		return this;
	}

	public DeleteQuery whereRaw(@NonNull String sql,
								Object... params) {
		whereClause.addRaw(sql, params);
		return this;
	}

	public DeleteQuery whereNull(@NonNull String col) {
		whereClause.addIsNull(col);
		return this;
	}

	public DeleteQuery whereNullable(@NonNull String col,
									 Object val) {
		whereClause.addNullable(col, val);
		return this;
	}

	@Override
	public String getSql() {

		StringBuilder sb = new StringBuilder("DELETE FROM ");

		if (database != null) sb.append(SqlUtils.quote(database)).append(".");
		sb.append(SqlUtils.quote(table));

		String where = whereClause.build();
		if (!where.isEmpty()) sb.append(" ").append(where);

		sb.append(";");
		return sb.toString();

	}

	@Override
	public List<Object> getParams() {
		return whereClause.getParams();
	}
}