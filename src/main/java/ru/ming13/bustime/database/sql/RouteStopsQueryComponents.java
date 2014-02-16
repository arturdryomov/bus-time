package ru.ming13.bustime.database.sql;

import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.SqlBuilder;

public final class RouteStopsQueryComponents implements QueryComponents
{
	private final long routeId;

	public RouteStopsQueryComponents(long routeId) {
		this.routeId = routeId;
	}

	@Override
	public String getTables() {
		return SqlBuilder.buildTableClause(
			DatabaseSchema.Tables.ROUTES_AND_STOPS,
			SqlBuilder.buildJoinClause(
				DatabaseSchema.Tables.ROUTES_AND_STOPS, DatabaseSchema.RoutesAndStopsColumns.STOP_ID,
				DatabaseSchema.Tables.STOPS, DatabaseSchema.StopsColumns._ID));
	}

	@Override
	public String[] getProjection() {
		return new String[]{
			BusTimeContract.Stops._ID,
			BusTimeContract.Stops.NAME,
			BusTimeContract.Stops.DIRECTION};
	}

	@Override
	public String getSelection() {
		return SqlBuilder.buildSelectionClause(DatabaseSchema.RoutesAndStopsColumns.ROUTE_ID);
	}

	@Override
	public String[] getSelectionArguments() {
		return new String[]{String.valueOf(routeId)};
	}

	@Override
	public String getSortOrder() {
		return SqlBuilder.buildSortOrderClause(
			DatabaseSchema.RoutesAndStopsColumns.SHIFT_HOUR,
			DatabaseSchema.RoutesAndStopsColumns.SHIFT_MINUTE);
	}
}
