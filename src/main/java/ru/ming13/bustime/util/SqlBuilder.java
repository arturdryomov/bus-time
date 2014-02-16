package ru.ming13.bustime.util;

import org.apache.commons.lang3.StringUtils;

public final class SqlBuilder
{
	private SqlBuilder() {
	}

	public static String buildAliasClause(String field, String alias) {
		return new StringBuilder()
			.append(field).append(" as ").append(alias)
			.toString();
	}

	public static String buildAttachClause(String file, String alias) {
		return new StringBuilder()
			.append("attach '").append(file).append("' as ").append(alias)
			.toString();
	}

	public static String buildCastIntegerClause(String field) {
		return new StringBuilder()
			.append("cast (").append(field).append(" as integer)")
			.toString();
	}

	public static String buildDeleteClause(String table) {
		return new StringBuilder()
			.append("delete from ").append(table)
			.toString();
	}

	public static String buildDetachClause(String alias) {
		return new StringBuilder()
			.append("detach ").append(alias)
			.toString();
	}

	public static String buildInsertClause(String table, String alias) {
		return new StringBuilder()
			.append("insert into ").append(table)
			.append(" select * from ").append(alias).append(".").append(table)
			.toString();
	}

	public static String buildIsNullClause(String field) {
		return new StringBuilder()
			.append(field).append(" is null")
			.toString();
	}

	public static String buildJoinClause(String sourceTable, String sourceField, String destinationTable, String destinationField) {
		return new StringBuilder()
			.append("inner join ").append(destinationTable).append(" on ")
			.append(destinationTable).append(".").append(destinationField)
			.append(" = ")
			.append(sourceTable).append(".").append(sourceField)
			.toString();
	}

	public static String buildLikeClause(String field, String query) {
		return new StringBuilder()
			.append(field).append(" like ").append("'%").append(query).append("%'")
			.toString();
	}

	public static String buildSortOrderClause(String... orderClauses) {
		return StringUtils.join(orderClauses, ",");
	}

	public static String buildOptionalSelectionClause(String... selectionClauses) {
		return StringUtils.join(selectionClauses, " or ");
	}

	public static String buildRequiredSelectionClause(String... selectionClauses) {
		return StringUtils.join(selectionClauses, " and ");
	}

	public static String buildSelectionClause(String field) {
		return new StringBuilder()
			.append(field).append(" = ?")
			.toString();
	}

	public static String buildSelectionClause(String firstField, String secondField) {
		return new StringBuilder()
			.append(firstField).append(" = ").append(secondField)
			.toString();
	}

	public static String buildTableClause(String... tableClauses) {
		return StringUtils.join(tableClauses, " ");
	}

	public static String buildTableField(String table, String field) {
		return new StringBuilder()
			.append(table).append(".").append(field)
			.toString();
	}
}
