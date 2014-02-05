package ru.ming13.bustime.database.sql;

import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.SqlBuilder;

public final class TimetableQueryComponents implements QueryComponents
{
	private final long routeId;
	private final long stopId;
	private final long tripTypeId;

	public TimetableQueryComponents(long routeId, long stopId, long tripTypeId) {
		this.routeId = routeId;
		this.stopId = stopId;
		this.tripTypeId = tripTypeId;
	}

	@Override
	public String getTables() {
		return SqlBuilder.buildTableClause(
			DatabaseSchema.Tables.TRIPS,
			SqlBuilder.buildJoinClause(
				DatabaseSchema.Tables.TRIPS, DatabaseSchema.TripsColumns.ROUTE_ID,
				DatabaseSchema.Tables.ROUTES_AND_STOPS, DatabaseSchema.RoutesAndStopsColumns.ROUTE_ID));
	}

	@Override
	public String[] getProjection() {
		return new String[]{
			BusTimeContract.Timetable._ID,
			getArrivalTimeColumn()};
	}

	private String getArrivalTimeColumn() {
		return new StringBuilder()
			.append("strftime('%Y-%m-%d %H:%M', 'now', 'localtime', 'start of day',")
			.append("+ (select ").append(DatabaseSchema.TripsColumns.HOUR).append(" || ' hours'), ")
			.append("+ (select ").append(DatabaseSchema.TripsColumns.MINUTE).append(" || ' minutes'), ")
			.append("+ (select ").append(DatabaseSchema.RoutesAndStopsColumns.SHIFT_HOUR).append(" || ' hours'), ")
			.append("+ (select ").append(DatabaseSchema.RoutesAndStopsColumns.SHIFT_MINUTE).append(" || ' minutes')) as ")
			.append(BusTimeContract.Timetable.ARRIVAL_TIME)
			.toString();
	}

	@Override
	public String getSelection() {
		return SqlBuilder.buildRequiredSelectionClause(
			SqlBuilder.buildSelectionClause(SqlBuilder.buildTableField(
				DatabaseSchema.Tables.ROUTES_AND_STOPS, DatabaseSchema.RoutesAndStopsColumns.ROUTE_ID)),
			SqlBuilder.buildSelectionClause(SqlBuilder.buildTableField(
				DatabaseSchema.Tables.ROUTES_AND_STOPS, DatabaseSchema.RoutesAndStopsColumns.STOP_ID)),
			SqlBuilder.buildSelectionClause(
				DatabaseSchema.TripsColumns.TYPE_ID));
	}

	@Override
	public String[] getSelectionArguments() {
		return new String[]{
			String.valueOf(routeId),
			String.valueOf(stopId),
			String.valueOf(tripTypeId)};
	}

	@Override
	public String getSortOrder() {
		return SqlBuilder.buildSortOrderClause(
			DatabaseSchema.TripsColumns.HOUR,
			DatabaseSchema.TripsColumns.MINUTE);
	}
}
