package ru.ming13.bustime.util;

import android.text.TextUtils;

public final class SqlBuilder
{
	private SqlBuilder() {
	}

	public static String buildAliasClause(String field, String alias) {
		return String.format("%s as %s", field, alias);
	}

	public static String buildCastIntegerClause(String field) {
		return String.format("cast (%s as integer)", field);
	}

	public static String buildConcatClause(String... fields) {
		return String.format("(select %s)", TextUtils.join(" || ", fields));
	}

	public static String buildInnerJoinClause(String sourceTable, String sourceField, String destinationTable, String destinationField) {
		return String.format("inner join %s on %s.%s = %s.%s",
			destinationTable,
			destinationTable, destinationField,
			sourceTable, sourceField);
	}

	public static String buildLikeClause(String field, String query) {
		return String.format("%s like '%%%s%%'", field, query);
	}

	public static String buildOrderClause(String... orderClauses) {
		return TextUtils.join(", ", orderClauses);
	}

	public static String buildOrderAscendingClause(String field) {
		return String.format("%s asc", field);
	}

	public static String buildOptionalSelectionClause(String... selectionClauses) {
		return TextUtils.join(" or ", selectionClauses);
	}

	public static String buildRequiredSelectionClause(String... selectionClauses) {
		return TextUtils.join(" and ", selectionClauses);
	}

	public static String buildSelectionClause(String field, long id) {
		return String.format("%s = %d", field, id);
	}

	public static String buildSelectionClause(String tableName, String field, long id) {
		return String.format("%s.%s = %d", tableName, field, id);
	}

	public static String buildTableClause(String... tableClauses) {
		return TextUtils.join(" ", tableClauses);
	}
}
