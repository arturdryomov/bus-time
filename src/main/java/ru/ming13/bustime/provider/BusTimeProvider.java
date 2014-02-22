package ru.ming13.bustime.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import ru.ming13.bustime.database.DatabaseOpenHelper;
import ru.ming13.bustime.database.sql.QueryComponents;
import ru.ming13.bustime.database.sql.RouteStopsQueryComponents;
import ru.ming13.bustime.database.sql.RoutesQueryComponents;
import ru.ming13.bustime.database.sql.StopsQueryComponents;
import ru.ming13.bustime.database.sql.StopsRoutesQueryComponents;
import ru.ming13.bustime.database.sql.StopsSearchQueryComponents;
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
		try {
			return query(uri);
		} catch (IllegalArgumentException e) {
			return null;
		} catch (SQLiteException e) {
			return null;
		}
	}

	private Cursor query(Uri uri) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		QueryComponents queryComponents = buildQueryComponents(uri);

		queryBuilder.setTables(queryComponents.getTables());

		Cursor cursor = queryBuilder.query(getDatabase(),
			queryComponents.getProjection(),
			queryComponents.getSelection(),
			queryComponents.getSelectionArguments(),
			null,
			null,
			queryComponents.getSortOrder());

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	private QueryComponents buildQueryComponents(Uri uri) {
		switch (uriMatcher.match(uri)) {
			case BusTimeUriMatcher.Codes.ROUTES:
				return buildRoutesQueryComponents();

			case BusTimeUriMatcher.Codes.STOPS:
				return buildStopsQueryComponents();

			case BusTimeUriMatcher.Codes.ROUTE_STOPS:
				return buildRouteStopsQueryComponents(uri);

			case BusTimeUriMatcher.Codes.STOP_ROUTES:
				return buildStopRoutesQueryComponents(uri);

			case BusTimeUriMatcher.Codes.TIMETABLE:
				return buildTimetableQueryComponents(uri);

			case BusTimeUriMatcher.Codes.STOPS_SEARCH:
				return buildStopsSearchQueryComponents(uri);

			default:
				throw new IllegalArgumentException(buildUnsupportedUriDetailMessage(uri));
		}
	}

	private QueryComponents buildRoutesQueryComponents() {
		return new RoutesQueryComponents();
	}

	private QueryComponents buildStopsQueryComponents() {
		return new StopsQueryComponents();
	}

	private QueryComponents buildRouteStopsQueryComponents(Uri uri) {
		long routeId = BusTimeContract.Routes.getRouteId(uri);

		return new RouteStopsQueryComponents(routeId);
	}

	private QueryComponents buildStopRoutesQueryComponents(Uri uri) {
		long stopId = BusTimeContract.Stops.getStopId(uri);
		long timetableTypeId = BusTimeContract.Timetable.Type.currentWeekPartDependent();

		return new StopsRoutesQueryComponents(stopId, timetableTypeId);
	}

	private QueryComponents buildTimetableQueryComponents(Uri uri) {
		long routeId = BusTimeContract.Timetable.getRouteId(uri);
		long stopId = BusTimeContract.Timetable.getStopId(uri);
		long typeId = BusTimeContract.Timetable.getTimetableType(uri);

		return new TimetableQueryComponents(routeId, stopId, typeId);
	}

	private QueryComponents buildStopsSearchQueryComponents(Uri uri) {
		String searchQuery = BusTimeContract.Stops.getStopsSearchQuery(uri);

		return new StopsSearchQueryComponents(searchQuery);
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
