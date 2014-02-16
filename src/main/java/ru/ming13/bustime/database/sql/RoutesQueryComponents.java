package ru.ming13.bustime.database.sql;

import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.SqlBuilder;

public class RoutesQueryComponents implements QueryComponents
{
	@Override
	public String getTables() {
		return DatabaseSchema.Tables.ROUTES;
	}

	@Override
	public String[] getProjection() {
		return new String[]{
			BusTimeContract.Routes._ID,
			BusTimeContract.Routes.NUMBER,
			BusTimeContract.Routes.DESCRIPTION};
	}

	@Override
	public String getSelection() {
		return null;
	}

	@Override
	public String[] getSelectionArguments() {
		return null;
	}

	@Override
	public String getSortOrder() {
		return SqlBuilder.buildSortOrderClause(
			SqlBuilder.buildCastIntegerClause(DatabaseSchema.RoutesColumns.NUMBER),
			DatabaseSchema.RoutesColumns.DESCRIPTION);
	}
}
