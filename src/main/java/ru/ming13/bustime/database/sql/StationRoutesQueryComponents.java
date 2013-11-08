package ru.ming13.bustime.database.sql;

import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.SqlBuilder;

public final class StationRoutesQueryComponents implements QueryComponents
{
	private final long stationId;
	private final long timetableTypeId;

	public StationRoutesQueryComponents(long stationId, long timetableTypeId) {
		this.stationId = stationId;
		this.timetableTypeId = timetableTypeId;
	}

	@Override
	public String getTables() {
		return SqlBuilder.buildTableClause(
			DatabaseSchema.Tables.ROUTES_AND_STATIONS,
			SqlBuilder.buildJoinClause(
				DatabaseSchema.Tables.ROUTES_AND_STATIONS, DatabaseSchema.RoutesAndStationsColumns.ROUTE_ID,
				DatabaseSchema.Tables.ROUTES, DatabaseSchema.RoutesColumns._ID));
	}

	@Override
	public String[] getProjection() {
		return new String[]{
			BusTimeContract.Routes._ID,
			BusTimeContract.Routes.NUMBER,
			BusTimeContract.Routes.DESCRIPTION,
			getArrivalTimeProjection()};
	}

	private String getArrivalTimeProjection() {
		return String.format("(select %s from %s where %s order by %s limit 1) as %s",
			getArrivalTimeColumn(),
			DatabaseSchema.Tables.TRIPS,
			SqlBuilder.buildRequiredSelectionClause(
				SqlBuilder.buildSelectionClause(
					DatabaseSchema.TripsColumns.ROUTE_ID,
					SqlBuilder.buildFullField(DatabaseSchema.Tables.ROUTES, DatabaseSchema.RoutesColumns._ID)),
				String.format("%s in (%d, %d)",
					DatabaseSchema.TripsColumns.TYPE_ID,
					DatabaseSchema.TripTypesColumnsValues.FULL_WEEK_ID, timetableTypeId),
				String.format("%s >= datetime('now', 'localtime')", BusTimeContract.Timetable.ARRIVAL_TIME)),
			SqlBuilder.buildSortOrderClause(
				DatabaseSchema.TripsColumns.HOUR, DatabaseSchema.TripsColumns.MINUTE),
			BusTimeContract.Timetable.ARRIVAL_TIME);
	}

	private String getArrivalTimeColumn() {
		return String.format("datetime('now', 'localtime', 'start of day', + %s, + %s, + %s, + %s) as %s",
			SqlBuilder.buildConcatClause(DatabaseSchema.TripsColumns.HOUR, "' hours'"),
			SqlBuilder.buildConcatClause(DatabaseSchema.TripsColumns.MINUTE, "' minutes'"),
			SqlBuilder.buildConcatClause(DatabaseSchema.RoutesAndStationsColumns.SHIFT_HOUR, "' hours'"),
			SqlBuilder.buildConcatClause(DatabaseSchema.RoutesAndStationsColumns.SHIFT_MINUTE, "' minutes'"),
			BusTimeContract.Timetable.ARRIVAL_TIME);
	}

	@Override
	public String getSelection() {
		return SqlBuilder.buildSelectionClause(DatabaseSchema.RoutesAndStationsColumns.STATION_ID);
	}

	@Override
	public String[] getSelectionArguments() {
		return new String[]{String.valueOf(stationId)};
	}

	@Override
	public String getSortOrder() {
		return BusTimeContract.Timetable.ARRIVAL_TIME;
	}
}
