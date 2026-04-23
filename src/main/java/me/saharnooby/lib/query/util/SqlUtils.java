package me.saharnooby.lib.query.util;

import lombok.NonNull;

public final class SqlUtils {

	private static final String IDENTIFIER_REGEX = "^[a-zA-Z0-9_]+$";

	private SqlUtils() {}

	/**
	 * Проверяет, что имя таблицы или колонки содержит только безопасные символы.
	 */
	public static void validateIdentifier(@NonNull String name) {
		if (name.isEmpty() || !name.matches(IDENTIFIER_REGEX)) {
			throw new IllegalArgumentException("Invalid SQL identifier: " + name);
		}
	}

	/**
	 * Оборачивает идентификатор в обратные кавычки.
	 */
	public static String quote(@NonNull String name) {
		return "`" + name + "`";
	}
}