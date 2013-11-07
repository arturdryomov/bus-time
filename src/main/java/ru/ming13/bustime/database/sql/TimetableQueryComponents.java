package ru.ming13.bustime.database.sql;

import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.SqlBuilder;

public final class TimetableQueryComponents implements QueryComponents
{
	private final long routeId;
	private final long stationId;
	private final int tripTypeId;

	public TimetableQueryComponents(long routeId, long stationId, int tripTypeId) {
		this.routeId = routeId;
		this.stationId = stationId;
		this.tripTypeId = tripTypeId;
	}

	@Override
	public String getTables() {
		return SqlBuilder.buildTableClause(
			DatabaseSchema.Tables.TRIPS,
			SqlBuilder.buildInnerJoinClause(
				DatabaseSchema.Tables.TRIPS, DatabaseSchema.TripsColumns.ROUTE_ID,
				DatabaseSchema.Tables.ROUTES_AND_STATIONS, DatabaseSchema.RoutesAndStationsColumns.ROUTE_ID));
	}

	@Override
	public String[] getProjection() {
		return new String[]{
			BusTimeContract.Timetable._ID,
			getArrivalTimeProjection()};
	}

	private String getArrivalTimeProjection() {
		return String.format("datetime('now', 'localtime', 'start of day', + %s, + %s, + %s, + %s) as %s",
			SqlBuilder.buildConcatClause(DatabaseSchema.TripsColumns.HOUR, "' hours'"),
			SqlBuilder.buildConcatClause(DatabaseSchema.TripsColumns.MINUTE, "' minutes'"),
			SqlBuilder.buildConcatClause(DatabaseSchema.RoutesAndStationsColumns.SHIFT_HOUR, "' hours'"),
			SqlBuilder.buildConcatClause(DatabaseSchema.RoutesAndStationsColumns.SHIFT_MINUTE, "' minutes'"),
			BusTimeContract.Timetable.ARRIVAL_TIME);
	}

	@Override
	public String getSelection() {
		return SqlBuilder.buildRequiredSelectionClause(
			SqlBuilder.buildFullSelectionClause(
				DatabaseSchema.Tables.ROUTES_AND_STATIONS, DatabaseSchema.RoutesAndStationsColumns.ROUTE_ID),
			SqlBuilder.buildFullSelectionClause(
				DatabaseSchema.Tables.ROUTES_AND_STATIONS, DatabaseSchema.RoutesAndStationsColumns.STATION_ID),
			SqlBuilder.buildFullSelectionClause(
				DatabaseSchema.Tables.TRIPS, DatabaseSchema.TripsColumns.TYPE_ID));
	}

	@Override
	public String[] getSelectionArguments() {
		return new String[]{
			String.valueOf(routeId), String.valueOf(stationId), String.valueOf(tripTypeId)};
	}

	@Override
	public String getSortOrder() {
		return SqlBuilder.buildSortOrderClause(
			DatabaseSchema.TripsColumns.HOUR,
			DatabaseSchema.TripsColumns.MINUTE);
	}
}
