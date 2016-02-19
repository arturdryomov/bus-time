package ru.ming13.bustime.database.sql;

import android.app.SearchManager;

import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.SqlBuilder;
import ru.ming13.bustime.util.Strings;

public class StopsSearchQueryComponents implements QueryComponents
{
	private final String searchQuery;

	public StopsSearchQueryComponents(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	@Override
	public String getTables() {
		return DatabaseSchema.Tables.STOPS;
	}

	@Override
	public String[] getProjection() {
		return new String[]{
			BusTimeContract.Stops._ID,
			SqlBuilder.buildAliasClause(DatabaseSchema.StopsColumns._ID, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID),
			SqlBuilder.buildAliasClause(DatabaseSchema.StopsColumns.NAME, SearchManager.SUGGEST_COLUMN_TEXT_1),
			SqlBuilder.buildAliasClause(DatabaseSchema.StopsColumns.DIRECTION, SearchManager.SUGGEST_COLUMN_TEXT_2)};
	}

	@Override
	public String getSelection() {
		return SqlBuilder.buildOptionalSelectionClause(
			SqlBuilder.buildLikeClause(DatabaseSchema.StopsColumns.NAME, searchQuery.toLowerCase()),
			SqlBuilder.buildLikeClause(DatabaseSchema.StopsColumns.NAME, Strings.capitalize(searchQuery.toLowerCase())));
	}

	@Override
	public String[] getSelectionArguments() {
		return null;
	}

	@Override
	public String getSortOrder() {
		return SqlBuilder.buildSortOrderClause(
			DatabaseSchema.StopsColumns.NAME,
			DatabaseSchema.StopsColumns.DIRECTION);
	}
}
