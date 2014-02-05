package ru.ming13.bustime.database.sql;

import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.SqlBuilder;

public final class StopsRoutesQueryComponents implements QueryComponents
{
	private final long stopId;
	private final long timetableTypeId;

	public StopsRoutesQueryComponents(long stopId, long timetableTypeId) {
		this.stopId = stopId;
		this.timetableTypeId = timetableTypeId;
	}

	@Override
	public String getTables() {
		return SqlBuilder.buildTableClause(
			DatabaseSchema.Tables.ROUTES_AND_STOPS,
			SqlBuilder.buildJoinClause(
				DatabaseSchema.Tables.ROUTES_AND_STOPS, DatabaseSchema.RoutesAndStopsColumns.ROUTE_ID,
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
		return new StringBuilder()
			.append("(select ")
				.append(getArrivalTimeColumn())
			.append(" from ")
				.append(DatabaseSchema.Tables.TRIPS)
			.append(" where ")
				.append(SqlBuilder.buildSelectionClause(
					DatabaseSchema.TripsColumns.ROUTE_ID,
					SqlBuilder.buildTableField(DatabaseSchema.Tables.ROUTES, DatabaseSchema.RoutesColumns._ID)))
				.append(" and ")
				.append(DatabaseSchema.TripsColumns.TYPE_ID).append(" in (")
					.append(DatabaseSchema.TripTypesColumnsValues.FULL_WEEK_ID)
					.append(",")
					.append(timetableTypeId)
					.append(")")
				.append(" and ")
				.append(BusTimeContract.Timetable.ARRIVAL_TIME).append(" >= strftime('%Y-%m-%d %H:%M', 'now', 'localtime')")
			.append(" order by ")
				.append(SqlBuilder.buildSortOrderClause(
					DatabaseSchema.TripsColumns.HOUR, DatabaseSchema.TripsColumns.MINUTE))
			.append(" limit 1) as ").append(BusTimeContract.Timetable.ARRIVAL_TIME)
			.toString();
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
		return SqlBuilder.buildSelectionClause(DatabaseSchema.RoutesAndStopsColumns.STOP_ID);
	}

	@Override
	public String[] getSelectionArguments() {
		return new String[]{String.valueOf(stopId)};
	}

	@Override
	public String getSortOrder() {
		return SqlBuilder.buildSortOrderClause(
			SqlBuilder.buildIsNullClause(BusTimeContract.Timetable.ARRIVAL_TIME),
			BusTimeContract.Timetable.ARRIVAL_TIME,
			SqlBuilder.buildCastIntegerClause(BusTimeContract.Routes.NUMBER),
			BusTimeContract.Routes.DESCRIPTION);
	}
}
