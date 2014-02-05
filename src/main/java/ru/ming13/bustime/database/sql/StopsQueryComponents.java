package ru.ming13.bustime.database.sql;

import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.SqlBuilder;

public final class StopsQueryComponents implements QueryComponents
{
	@Override
	public String getTables() {
		return DatabaseSchema.Tables.STOPS;
	}

	@Override
	public String[] getProjection() {
		return new String[]{
			BusTimeContract.Stops._ID,
			BusTimeContract.Stops.NAME,
			BusTimeContract.Stops.DIRECTION,
			BusTimeContract.Stops.LATITUDE,
			BusTimeContract.Stops.LONGITUDE};
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
			DatabaseSchema.StopsColumns.NAME,
			DatabaseSchema.StopsColumns.DIRECTION);
	}
}
