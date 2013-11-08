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

	public static String buildJoinClause(String sourceTable, String sourceField, String destinationTable, String destinationField) {
		return String.format("inner join %s on %s.%s = %s.%s",
			destinationTable,
			destinationTable, destinationField,
			sourceTable, sourceField);
	}

	public static String buildLikeClause(String field, String query) {
		return String.format("%s like '%%%s%%'", field, query);
	}

	public static String buildSortOrderClause(String... orderClauses) {
		return TextUtils.join(",", orderClauses);
	}

	public static String buildOptionalSelectionClause(String... selectionClauses) {
		return TextUtils.join(" or ", selectionClauses);
	}

	public static String buildRequiredSelectionClause(String... selectionClauses) {
		return TextUtils.join(" and ", selectionClauses);
	}

	public static String buildSelectionClause(String field) {
		return String.format("%s = ?", field);
	}

	public static String buildSelectionClause(String firstField, String secondField) {
		return String.format("%s = %s", firstField, secondField);
	}

	public static String buildFullSelectionClause(String tableName, String field) {
		return String.format("%s.%s = ?", tableName, field);
	}

	public static String buildTableClause(String... tableClauses) {
		return TextUtils.join(" ", tableClauses);
	}

	public static String buildFullField(String tableName, String field) {
		return String.format("%s.%s", tableName, field);
	}
}
