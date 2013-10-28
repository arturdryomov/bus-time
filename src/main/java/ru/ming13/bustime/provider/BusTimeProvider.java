package ru.ming13.bustime.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.Calendar;

import ru.ming13.bustime.database.DatabaseOpenHelper;
import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.util.SqlBuilder;

public class BusTimeProvider extends ContentProvider
{
	private SQLiteOpenHelper databaseHelper;

	private UriMatcher uriMatcher;

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseOpenHelper(getContext());

		uriMatcher = BusTimeProviderPaths.buildUriMatcher();

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArguments, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		switch (uriMatcher.match(uri)) {
			case BusTimeProviderPaths.Codes.ROUTES:
				queryBuilder.setTables(buildRoutesTableClause());
				sortOrder = buildRoutesSortOrder();
				break;

			case BusTimeProviderPaths.Codes.STATIONS:
				queryBuilder.setTables(buildStationsTableClause());
				sortOrder = buildStationsSortOrder();
				break;

			case BusTimeProviderPaths.Codes.ROUTE_STATIONS:
				queryBuilder.setTables(buildRouteStationsTableClause());
				queryBuilder.appendWhere(buildRouteStationsSelectionClause(uri));
				break;

			case BusTimeProviderPaths.Codes.STATION_ROUTES:
				queryBuilder.setTables(buildStationRoutesTableClause());
				queryBuilder.appendWhere(buildStationRoutesSelectionClause(uri));
				break;

			case BusTimeProviderPaths.Codes.ROUTE_TIMETABLE:
				queryBuilder.setTables(buildTimetableTableClause());
				queryBuilder.appendWhere(buildRouteTimetableSelectionClause(uri));
				projection = buildTimetableProjection();
				sortOrder = buildTimetableSortOrder();
				break;

			case BusTimeProviderPaths.Codes.STATION_TIMETABLE:
				queryBuilder.setTables(buildTimetableTableClause());
				queryBuilder.appendWhere(buildStationTimetableSelectionClause(uri));
				projection = buildTimetableProjection();
				sortOrder = buildTimetableSortOrder();
				break;

			default:
				throw new IllegalArgumentException(buildUnsupportedUriDetailMessage(uri));
		}

		return queryBuilder.query(getDatabase(), projection, selection, selectionArguments, null, null, sortOrder);
	}

	private String buildRoutesTableClause() {
		return DatabaseSchema.Tables.ROUTES;
	}

	private String buildRoutesSortOrder() {
		return SqlBuilder.buildCastIntegerClause(DatabaseSchema.RoutesColumns.NUMBER);
	}

	private String buildStationsTableClause() {
		return DatabaseSchema.Tables.STATIONS;
	}

	private String buildStationsSortOrder() {
		return DatabaseSchema.StationsColumns.NAME;
	}

	private String buildRouteStationsTableClause() {
		String stationsJoinClause = SqlBuilder.buildInnerJoinClause(
			DatabaseSchema.Tables.ROUTES_AND_STATIONS, DatabaseSchema.RoutesAndStationsColumns.STATION_ID,
			DatabaseSchema.Tables.STATIONS, DatabaseSchema.StationsColumns._ID);

		return SqlBuilder.buildTableClause(DatabaseSchema.Tables.ROUTES_AND_STATIONS, stationsJoinClause);
	}

	private String buildRouteStationsSelectionClause(Uri uri) {
		long routeId = BusTimeContract.Routes.getRouteId(uri);

		return SqlBuilder.buildSelectionClause(DatabaseSchema.RoutesAndStationsColumns.ROUTE_ID, routeId);
	}

	private String buildStationRoutesTableClause() {
		String routesJoinClause = SqlBuilder.buildInnerJoinClause(
			DatabaseSchema.Tables.ROUTES_AND_STATIONS, DatabaseSchema.RoutesAndStationsColumns.ROUTE_ID,
			DatabaseSchema.Tables.ROUTES, DatabaseSchema.RoutesColumns._ID);

		return SqlBuilder.buildTableClause(DatabaseSchema.Tables.ROUTES_AND_STATIONS, routesJoinClause);
	}

	private String buildStationRoutesSelectionClause(Uri uri) {
		long stationId = BusTimeContract.Stations.getStationId(uri);

		return SqlBuilder.buildSelectionClause(DatabaseSchema.RoutesAndStationsColumns.STATION_ID, stationId);
	}

	private String buildTimetableTableClause() {
		String routesAndStationsJoinClause = SqlBuilder.buildInnerJoinClause(
			DatabaseSchema.Tables.TRIPS, DatabaseSchema.TripsColumns.ROUTE_ID,
			DatabaseSchema.Tables.ROUTES_AND_STATIONS, DatabaseSchema.RoutesAndStationsColumns.ROUTE_ID);

		return SqlBuilder.buildTableClause(DatabaseSchema.Tables.TRIPS, routesAndStationsJoinClause);
	}

	private String buildRouteTimetableSelectionClause(Uri uri) {
		long routeId = BusTimeContract.Routes.getTimetableRouteId(uri);
		long stationId = BusTimeContract.Routes.getTimetableStationId(uri);
		int typeId = getTimetableTypeId(routeId);

		return buildTimetableSelectionClause(routeId, stationId, typeId);
	}

	private int getTimetableTypeId(long routeId) {
		if (isTimetableWeekDayIndependent(routeId)) {
			return DatabaseSchema.TripTypesColumnsValues.FULL_WEEK_ID;
		}

		if (isTodayWeekend()) {
			return DatabaseSchema.TripTypesColumnsValues.WEEKEND_ID;
		} else {
			return DatabaseSchema.TripTypesColumnsValues.WORKDAY_ID;
		}
	}

	private boolean isTimetableWeekDayIndependent(long routeId) {
		String fullWeekTripsCountingQuery = buildFullWeekTripsCountingQuery(routeId);

		return DatabaseUtils.longForQuery(getDatabase(), fullWeekTripsCountingQuery, null) != 0;
	}

	private String buildFullWeekTripsCountingQuery(long routeId) {
		return String.format("select count(%s) from %s where %s = %d and %s = %d",
			DatabaseSchema.TripsColumns._ID, DatabaseSchema.Tables.TRIPS,
			DatabaseSchema.TripsColumns.ROUTE_ID, routeId,
			DatabaseSchema.TripsColumns.TYPE_ID, DatabaseSchema.TripTypesColumnsValues.FULL_WEEK_ID);
	}

	private boolean isTodayWeekend() {
		int weekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

		return (weekDay == Calendar.SATURDAY) || (weekDay == Calendar.SUNDAY);
	}

	private String buildTimetableSelectionClause(long routeId, long stationId, int typeId) {
		String routeSelectionClause = SqlBuilder.buildSelectionClause(
			DatabaseSchema.Tables.ROUTES_AND_STATIONS, DatabaseSchema.RoutesAndStationsColumns.ROUTE_ID, routeId);
		String stationSelectionClause = SqlBuilder.buildSelectionClause(
			DatabaseSchema.Tables.ROUTES_AND_STATIONS, DatabaseSchema.RoutesAndStationsColumns.STATION_ID, stationId);
		String timetableSelectionClause = SqlBuilder.buildSelectionClause(
			DatabaseSchema.Tables.TRIPS, DatabaseSchema.TripsColumns.TYPE_ID, typeId);

		return SqlBuilder.buildSelectionClause(routeSelectionClause, stationSelectionClause, timetableSelectionClause);
	}

	private String[] buildTimetableProjection() {
		return new String[]{
			DatabaseSchema.TripsColumns._ID,
			buildArrivalTimeProjection()};
	}

	private String buildArrivalTimeProjection() {
		return String.format("datetime('now', 'localtime', 'start of day', + %s, + %s, + %s, + %s) as %s",
			SqlBuilder.buildConcatClause(DatabaseSchema.TripsColumns.HOUR, "' hours'"),
			SqlBuilder.buildConcatClause(DatabaseSchema.TripsColumns.MINUTE, "' minutes'"),
			SqlBuilder.buildConcatClause(DatabaseSchema.RoutesAndStationsColumns.SHIFT_HOUR, "' hours'"),
			SqlBuilder.buildConcatClause(DatabaseSchema.RoutesAndStationsColumns.SHIFT_MINUTE, "' minutes'"),
			BusTimeContract.Timetable.ARRIVAL_TIME);
	}

	private String buildTimetableSortOrder() {
		return SqlBuilder.buildOrderClause(
			SqlBuilder.buildOrderAscendingClause(DatabaseSchema.TripsColumns.HOUR),
			SqlBuilder.buildOrderAscendingClause(DatabaseSchema.TripsColumns.MINUTE));
	}

	private String buildStationTimetableSelectionClause(Uri uri) {
		long routeId = BusTimeContract.Stations.getTimetableRouteId(uri);
		long stationId = BusTimeContract.Stations.getTimetableStationId(uri);
		int typeId = getTimetableTypeId(routeId);

		return buildTimetableSelectionClause(routeId, stationId, typeId);
	}

	private String buildUnsupportedUriDetailMessage(Uri unsupportedUri) {
		return String.format("Unsupported URI: %s", Uri.decode(unsupportedUri.toString()));
	}

	private SQLiteDatabase getDatabase() {
		return databaseHelper.getReadableDatabase();
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArguments) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArguments) {
		return 0;
	}
}
