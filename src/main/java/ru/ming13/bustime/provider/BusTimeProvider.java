package ru.ming13.bustime.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import ru.ming13.bustime.database.DatabaseOpenHelper;
import ru.ming13.bustime.database.sql.QueryComponents;
import ru.ming13.bustime.database.sql.RouteStationsQueryComponents;
import ru.ming13.bustime.database.sql.RoutesQueryComponents;
import ru.ming13.bustime.database.sql.StationRoutesQueryComponents;
import ru.ming13.bustime.database.sql.StationsQueryComponents;
import ru.ming13.bustime.database.sql.StationsSearchQueryComponents;
import ru.ming13.bustime.database.sql.TimetableQueryComponents;

public class BusTimeProvider extends ContentProvider
{
	private SQLiteOpenHelper databaseHelper;
	private UriMatcher uriMatcher;

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseOpenHelper(getContext());
		uriMatcher = BusTimeUriMatcher.buildMatcher();

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArguments, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		QueryComponents queryComponents = buildQueryComponents(uri);

		queryBuilder.setTables(queryComponents.getTables());

		return queryBuilder.query(getDatabase(),
			queryComponents.getProjection(),
			queryComponents.getSelection(),
			queryComponents.getSelectionArguments(),
			null,
			null,
			queryComponents.getSortOrder());
	}

	private QueryComponents buildQueryComponents(Uri uri) {
		switch (uriMatcher.match(uri)) {
			case BusTimeUriMatcher.Codes.ROUTES:
				return buildRoutesQueryComponents();

			case BusTimeUriMatcher.Codes.STATIONS:
				return buildStationsQueryComponents();

			case BusTimeUriMatcher.Codes.ROUTE_STATIONS:
				return buildRouteStationsQueryComponents(uri);

			case BusTimeUriMatcher.Codes.STATION_ROUTES:
				return buildStationRoutesQueryComponents(uri);

			case BusTimeUriMatcher.Codes.ROUTE_TIMETABLE:
				return buildRouteTimetableQueryComponents(uri);

			case BusTimeUriMatcher.Codes.STATION_TIMETABLE:
				return buildStationTimetableQueryComponents(uri);

			case BusTimeUriMatcher.Codes.STATIONS_SEARCH:
				return buildStationsSearchQueryComponents(uri);

			default:
				throw new IllegalArgumentException(buildUnsupportedUriDetailMessage(uri));
		}
	}

	private QueryComponents buildRoutesQueryComponents() {
		return new RoutesQueryComponents();
	}

	private QueryComponents buildStationsQueryComponents() {
		return new StationsQueryComponents();
	}

	private QueryComponents buildRouteStationsQueryComponents(Uri uri) {
		long routeId = BusTimeContract.Routes.getStationsRouteId(uri);

		return new RouteStationsQueryComponents(routeId);
	}

	private QueryComponents buildStationRoutesQueryComponents(Uri uri) {
		long stationId = BusTimeContract.Stations.getRoutesStationId(uri);
		long timetableTypeId = BusTimeContract.Timetable.Type.currentWeekPartDependent();

		return new StationRoutesQueryComponents(stationId, timetableTypeId);
	}

	private QueryComponents buildRouteTimetableQueryComponents(Uri uri) {
		long routeId = BusTimeContract.Routes.getTimetableRouteId(uri);
		long stationId = BusTimeContract.Routes.getTimetableStationId(uri);
		long typeId = BusTimeContract.Timetable.getTimetableType(uri);

		return new TimetableQueryComponents(routeId, stationId, typeId);
	}

	private QueryComponents buildStationTimetableQueryComponents(Uri uri) {
		long routeId = BusTimeContract.Stations.getTimetableRouteId(uri);
		long stationId = BusTimeContract.Stations.getTimetableStationId(uri);
		long typeId = BusTimeContract.Timetable.getTimetableType(uri);

		return new TimetableQueryComponents(routeId, stationId, typeId);
	}

	private QueryComponents buildStationsSearchQueryComponents(Uri uri) {
		String searchQuery = BusTimeContract.Stations.getSearchStationQuery(uri);

		return new StationsSearchQueryComponents(searchQuery);
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
