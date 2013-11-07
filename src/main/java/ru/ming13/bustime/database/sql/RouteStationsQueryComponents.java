package ru.ming13.bustime.database.sql;

import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.SqlBuilder;

public final class RouteStationsQueryComponents implements QueryComponents
{
	private final long routeId;

	public RouteStationsQueryComponents(long routeId) {
		this.routeId = routeId;
	}

	@Override
	public String getTables() {
		return SqlBuilder.buildTableClause(
			DatabaseSchema.Tables.ROUTES_AND_STATIONS,
			SqlBuilder.buildInnerJoinClause(
				DatabaseSchema.Tables.ROUTES_AND_STATIONS, DatabaseSchema.RoutesAndStationsColumns.STATION_ID,
				DatabaseSchema.Tables.STATIONS, DatabaseSchema.StationsColumns._ID));
	}

	@Override
	public String[] getProjection() {
		return new String[]{
			BusTimeContract.Stations._ID,
			BusTimeContract.Stations.NAME,
			BusTimeContract.Stations.DIRECTION};
	}

	@Override
	public String getSelection() {
		return SqlBuilder.buildFullSelectionClause(
			DatabaseSchema.Tables.ROUTES_AND_STATIONS, DatabaseSchema.RoutesAndStationsColumns.ROUTE_ID);
	}

	@Override
	public String[] getSelectionArguments() {
		return new String[]{
			String.valueOf(routeId)};
	}

	@Override
	public String getSortOrder() {
		return SqlBuilder.buildSortOrderClause(
			DatabaseSchema.RoutesAndStationsColumns.SHIFT_HOUR,
			DatabaseSchema.RoutesAndStationsColumns.SHIFT_MINUTE);
	}
}
