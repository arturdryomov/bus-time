package ru.ming13.bustime.database.sql;

import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.SqlBuilder;

public final class StationRoutesQueryComponents implements QueryComponents
{
	private static final String TABLE_ALIAS_TIMETABLE = "Timetable";

	private final long stationId;
	private final long timetableTypeId;

	public StationRoutesQueryComponents(long stationId, long timetableTypeId) {
		this.stationId = stationId;
		this.timetableTypeId = timetableTypeId;
	}

	@Override
	public String getTables() {
		return String.format("(select %s from %s) as %s",
			getTimetableAliasProjection(),
			SqlBuilder.buildTableClause(
				DatabaseSchema.Tables.STATIONS,
				SqlBuilder.buildInnerJoinClause(
					DatabaseSchema.Tables.STATIONS, DatabaseSchema.StationsColumns._ID,
					DatabaseSchema.Tables.ROUTES_AND_STATIONS, DatabaseSchema.RoutesAndStationsColumns.STATION_ID),
				SqlBuilder.buildInnerJoinClause(
					DatabaseSchema.Tables.ROUTES_AND_STATIONS, DatabaseSchema.RoutesAndStationsColumns.ROUTE_ID,
					DatabaseSchema.Tables.ROUTES, DatabaseSchema.RoutesColumns._ID)),
			TABLE_ALIAS_TIMETABLE);
	}

	private String getTimetableAliasProjection() {
		return String.format("%s, %s, %s, %s, %s",
			SqlBuilder.buildAliasClause(
				SqlBuilder.buildFullField(DatabaseSchema.Tables.ROUTES_AND_STATIONS, DatabaseSchema.RoutesAndStationsColumns.STATION_ID),
				DatabaseSchema.RoutesAndStationsColumns.STATION_ID),
			SqlBuilder.buildAliasClause(
				SqlBuilder.buildFullField(DatabaseSchema.Tables.ROUTES, DatabaseSchema.RoutesColumns._ID),
				DatabaseSchema.RoutesColumns._ID),
			SqlBuilder.buildAliasClause(
				SqlBuilder.buildFullField(DatabaseSchema.Tables.ROUTES, DatabaseSchema.RoutesColumns.NUMBER),
				DatabaseSchema.RoutesColumns.NUMBER),
			SqlBuilder.buildAliasClause(
				SqlBuilder.buildFullField(DatabaseSchema.Tables.ROUTES, DatabaseSchema.RoutesColumns.DESCRIPTION),
				DatabaseSchema.RoutesColumns.DESCRIPTION),
			getArrivalTimeAliasTable());
	}

	private String getArrivalTimeAliasTable() {
		return String.format("(select %s from %s where %s order by %s limit 1) as %s",
			getArrivalTimeProjection(),
			DatabaseSchema.Tables.TRIPS,
			SqlBuilder.buildRequiredSelectionClause(
				SqlBuilder.buildSelectionClause(
					SqlBuilder.buildFullField(DatabaseSchema.Tables.TRIPS, DatabaseSchema.TripsColumns.ROUTE_ID),
					SqlBuilder.buildFullField(DatabaseSchema.Tables.ROUTES, DatabaseSchema.RoutesColumns._ID)),
				String.format("%s in (%d, %d)",
					SqlBuilder.buildFullField(DatabaseSchema.Tables.TRIPS, DatabaseSchema.TripsColumns.TYPE_ID),
					DatabaseSchema.TripTypesColumnsValues.FULL_WEEK_ID, timetableTypeId),
				String.format("%s >= datetime('now', 'localtime')", BusTimeContract.Timetable.ARRIVAL_TIME)),
			SqlBuilder.buildSortOrderClause(
				DatabaseSchema.TripsColumns.HOUR, DatabaseSchema.TripsColumns.MINUTE),
			BusTimeContract.Timetable.ARRIVAL_TIME);
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
	public String[] getProjection() {
		return new String[]{
			SqlBuilder.buildAliasClause(
				SqlBuilder.buildFullField(TABLE_ALIAS_TIMETABLE, DatabaseSchema.RoutesColumns._ID),
				BusTimeContract.Routes._ID),
			SqlBuilder.buildAliasClause(
				SqlBuilder.buildFullField(TABLE_ALIAS_TIMETABLE, DatabaseSchema.RoutesColumns.NUMBER),
				BusTimeContract.Routes.NUMBER),
			SqlBuilder.buildAliasClause(
				SqlBuilder.buildFullField(TABLE_ALIAS_TIMETABLE, DatabaseSchema.RoutesColumns.DESCRIPTION),
				BusTimeContract.Routes.DESCRIPTION),
			SqlBuilder.buildAliasClause(
				SqlBuilder.buildFullField(TABLE_ALIAS_TIMETABLE, BusTimeContract.Timetable.ARRIVAL_TIME),
				BusTimeContract.Timetable.ARRIVAL_TIME)};
	}

	@Override
	public String getSelection() {
		return SqlBuilder.buildFullSelectionClause(
			TABLE_ALIAS_TIMETABLE, DatabaseSchema.RoutesAndStationsColumns.STATION_ID);
	}

	@Override
	public String[] getSelectionArguments() {
		return new String[]{
			String.valueOf(stationId)};
	}

	@Override
	public String getSortOrder() {
		return BusTimeContract.Timetable.ARRIVAL_TIME;
	}
}
