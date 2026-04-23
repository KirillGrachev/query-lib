package me.saharnooby.lib.query.query.impl;

import lombok.NonNull;
import me.saharnooby.lib.query.query.api.SqlQuery;
import me.saharnooby.lib.query.query.components.WhereClause;
import me.saharnooby.lib.query.util.SqlUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * SELECT запрос
 */
public class SelectQuery implements SqlQuery {

	private final String database;
	private final String table;

	private final List<String> selectFields = new ArrayList<>();
	private final WhereClause whereClause = new WhereClause();

	private String orderBy;
	private boolean orderDesc;
	private Long limit;
	private Long offset;
	private boolean selectAll;

	private SelectQuery(String database,
						String table) {
		this.database = database;
		this.table = table;
	}

	public static SelectQuery from(@NonNull String table) {
		return new SelectQuery(null, table);
	}

	public static SelectQuery from(@NonNull String db,
								   @NonNull String table) {
		return new SelectQuery(db, table);
	}

	public SelectQuery select(@NonNull String... fields) {

		if (selectAll) throw new IllegalStateException("Cannot add fields after selectAll()");

		for (String f : fields) {
			SqlUtils.validateIdentifier(f);
			selectFields.add(SqlUtils.quote(f));
		}

		return this;

	}

	public SelectQuery selectAll() {

		if (!selectFields.isEmpty())
			throw new IllegalStateException("Fields already specified");

		this.selectAll = true;
		return this;

	}

	public SelectQuery where(@NonNull String col,
							 @NonNull Object val) {
		whereClause.addEquality(col, val);
		return this;
	}

	public SelectQuery whereRaw(@NonNull String sql,
								Object... params) {
		whereClause.addRaw(sql, params);
		return this;
	}

	public SelectQuery whereNull(@NonNull String col) {
		whereClause.addIsNull(col);
		return this;
	}

	public SelectQuery whereNullable(@NonNull String col,
									 Object val) {
		whereClause.addNullable(col, val);
		return this;
	}

	public SelectQuery orderBy(@NonNull String col,
							   boolean desc) {

		SqlUtils.validateIdentifier(col);

		this.orderBy = col;
		this.orderDesc = desc;

		return this;

	}

	public SelectQuery limit(long limit) {
		this.limit = limit;
		return this;
	}

	public SelectQuery offset(long offset) {
		this.offset = offset;
		return this;
	}

	@Override
	public String getSql() {

		StringBuilder sb = new StringBuilder("SELECT ");

		if (selectAll) sb.append("*");
		else {

			if (selectFields.isEmpty())
				throw new IllegalStateException("No fields selected");
			sb.append(String.join(", ", selectFields));

		}

		sb.append(" FROM ");
		if (database != null)
			sb.append(SqlUtils.quote(database)).append(".");
		sb.append(SqlUtils.quote(table));

		String where = whereClause.build();
		if (!where.isEmpty()) sb.append(" ").append(where);

		if (orderBy != null) {
			sb.append(" ORDER BY ").append(SqlUtils.quote(orderBy));
			if (orderDesc) sb.append(" DESC");
		}

		if (limit != null) sb.append(" LIMIT ").append(limit);
		if (offset != null) sb.append(" OFFSET ").append(offset);

		sb.append(";");
		return sb.toString();

	}

	@Override
	public List<Object> getParams() {
		return whereClause.getParams();
	}
}