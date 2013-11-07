package ru.ming13.bustime.database.sql;

import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.SqlBuilder;

public final class StationsQueryComponents implements QueryComponents
{
	@Override
	public String getTables() {
		return DatabaseSchema.Tables.STATIONS;
	}

	@Override
	public String[] getProjection() {
		return new String[]{
			BusTimeContract.Stations._ID,
			BusTimeContract.Stations.NAME,
			BusTimeContract.Stations.DIRECTION,
			BusTimeContract.Stations.LATITUDE,
			BusTimeContract.Stations.LONGITUDE};
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
			DatabaseSchema.StationsColumns.NAME,
			DatabaseSchema.StationsColumns.DIRECTION);
	}
}
