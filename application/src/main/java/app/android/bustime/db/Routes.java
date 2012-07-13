package app.android.bustime.db;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Routes
{
	private final SQLiteDatabase database;

	Routes() {
		database = DbProvider.getInstance().getDatabase();
	}

	public List<Route> getRoutesList() {
		List<Route> routesList = new ArrayList<Route>();

		Cursor databaseCursor = database.rawQuery(buildRoutesSelectionQuery(), null);

		while (databaseCursor.moveToNext()) {
			ContentValues databaseValues = extractRouteDatabaseValuesFromCursor(databaseCursor);
			routesList.add(new Route(databaseValues));
		}

		databaseCursor.close();

		return routesList;
	}

	private String buildRoutesSelectionQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");

		queryBuilder.append(String.format("%s, ", DbFieldNames.ID));
		queryBuilder.append(String.format("%s ", DbFieldNames.NAME));

		queryBuilder.append(String.format("from %s ", DbTableNames.ROUTES));
		queryBuilder.append(String.format("order by %s", DbFieldNames.NAME));

		return queryBuilder.toString();
	}

	private ContentValues extractRouteDatabaseValuesFromCursor(Cursor databaseCursor) {
		ContentValues databaseValues = new ContentValues();

		long id = databaseCursor.getLong(databaseCursor.getColumnIndexOrThrow(DbFieldNames.ID));
		databaseValues.put(DbFieldNames.ID, id);

		String name = databaseCursor.getString(databaseCursor.getColumnIndexOrThrow(DbFieldNames.NAME));
		databaseValues.put(DbFieldNames.NAME, name);

		return databaseValues;
	}

	public List<Route> getRoutesList(Station station) {
		List<Route> routesList = new ArrayList<Route>();

		Cursor databaseCursor = database.rawQuery(buildRoutesByStationSelectionQuery(station), null);

		while (databaseCursor.moveToNext()) {
			ContentValues databaseValues = extractRouteDatabaseValuesFromCursor(databaseCursor);
			routesList.add(new Route(databaseValues));
		}

		databaseCursor.close();

		return routesList;
	}

	private String buildRoutesByStationSelectionQuery(Station station) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select distinct ");

		queryBuilder.append(String.format("%s.%s, ", DbTableNames.ROUTES, DbFieldNames.ID));
		queryBuilder.append(String.format("%s.%s ", DbTableNames.ROUTES, DbFieldNames.NAME));

		queryBuilder.append(String.format("from %s ", DbTableNames.ROUTES));
		queryBuilder.append(String.format("inner join %s ", DbTableNames.ROUTES_AND_STATIONS));
		queryBuilder.append(String.format("on %s.%s = %s.%s ", DbTableNames.ROUTES, DbFieldNames.ID,
			DbTableNames.ROUTES_AND_STATIONS, DbFieldNames.ROUTE_ID));

		queryBuilder.append(
			String.format("where %s.%s = %d ", DbTableNames.ROUTES_AND_STATIONS, DbFieldNames.STATION_ID,
				station.getId()));

		queryBuilder.append(String.format("order by %s.%s", DbTableNames.ROUTES, DbFieldNames.NAME));

		return queryBuilder.toString();
	}

	public void beginTransaction() {
		database.beginTransaction();
	}

	public void endTransaction() {
		database.endTransaction();
	}
}
