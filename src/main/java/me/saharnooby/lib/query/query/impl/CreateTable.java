package me.saharnooby.lib.query.query.impl;

import lombok.NonNull;
import me.saharnooby.lib.query.query.api.SqlQuery;
import me.saharnooby.lib.query.util.SqlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public final class CreateTable implements SqlQuery {

	private final String database;
	private final String table;
	private boolean ifNotExists;

	private final List<ColumnDefinition> columns = new ArrayList<>();

	private CreateTable(String database, String table) {
		this.database = database;
		this.table = table;
	}

	public static CreateTable table(@NonNull String tableName) {
		return new CreateTable(null, tableName);
	}

	public static CreateTable table(@NonNull String database,
									@NonNull String tableName) {
		return new CreateTable(database, tableName);
	}

	public CreateTable ifNotExists() {
		this.ifNotExists = true;
		return this;
	}

	public ColumnBuilder addColumn(@NonNull String name,
								   @NonNull String type) {

		SqlUtils.validateIdentifier(name);
		ColumnDefinition col = new ColumnDefinition(name, type);

		this.columns.add(col);
		return new ColumnBuilder(col);

	}

	public ColumnBuilder intKey(@NonNull String name) {
		return addColumn(name, "INT").notNull()
				.autoIncrement().primaryKey();
	}

	public ColumnBuilder bigint(@NonNull String name) {
		return addColumn(name, "BIGINT");
	}

	public ColumnBuilder varchar(@NonNull String name, int size) {
		return addColumn(name, "VARCHAR(" + size + ")");
	}

	public ColumnBuilder text(@NonNull String name) {
		return addColumn(name, "TEXT");
	}

	public ColumnBuilder bool(@NonNull String name) {
		return addColumn(name, "TINYINT(1)");
	}

	@Override
	public String getSql() {

		if (columns.isEmpty()) {
			throw new IllegalStateException("Cannot create table without columns");
		}

		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		if (ifNotExists) sb.append("IF NOT EXISTS ");


		if (database != null) {
			sb.append(SqlUtils.quote(database)).append(".");
		}

		sb.append(SqlUtils.quote(table)).append(" (\n");

		List<String> colDefinitions = columns.stream()
				.map(ColumnDefinition::toSql)
				.collect(Collectors.toList());

		sb.append(String.join(",\n", colDefinitions));

		sb.append("\n);");
		return sb.toString();

	}

	@Override
	public List<Object> getParams() {
		// DDL запросы обычно не имеют параметров prepared statement
		return List.of();
	}

	private static class ColumnDefinition {

		private final String name;
		private final String type;

		private boolean notNull;
		private boolean autoIncrement;
		private boolean primaryKey;

		private String defaultValue; // Упрощено до строки для DDL

		private ColumnDefinition(String name,
								 String type) {
			this.name = name;
			this.type = type;
		}

		@NonNull String toSql() {

			StringBuilder sb = new StringBuilder();
			sb.append(SqlUtils.quote(name)).append(" ").append(type);

			if (primaryKey) {
				sb.append(" PRIMARY KEY");
			}

			if (autoIncrement) {
				sb.append(" AUTO_INCREMENT");
			}

			if (notNull) {
				sb.append(" NOT NULL");
			}

			if (defaultValue != null) {
				sb.append(" DEFAULT ").append(defaultValue);
			}

			return sb.toString();

		}
	}

	public class ColumnBuilder {

		private final ColumnDefinition col;

		private ColumnBuilder(ColumnDefinition col) {
			this.col = col;
		}

		public ColumnBuilder notNull() {
			this.col.notNull = true;
			return this;
		}

		public ColumnBuilder autoIncrement() {
			this.col.autoIncrement = true;
			return this;
		}

		public ColumnBuilder primaryKey() {
			this.col.primaryKey = true;
			return this;
		}

		public ColumnBuilder defaultValue(@NonNull String val) {
			this.col.defaultValue = "'" + val.replace("'", "''") + "'";
			return this;
		}

		public ColumnBuilder defaultValueRaw(String sqlExpr) {
			this.col.defaultValue = sqlExpr;
			return this;
		}

		public CreateTable endColumn() {
			return CreateTable.this;
		}
	}
}