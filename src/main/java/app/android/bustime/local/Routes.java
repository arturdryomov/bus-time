package app.android.bustime.local;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Routes
{
	private final SQLiteDatabase database;

	public Routes() {
		database = DbProvider.getInstance().getDatabase();
	}

	public List<Route> getRoutesList() {
		List<Route> routesList = new ArrayList<Route>();

		Cursor databaseCursor = database.rawQuery(buildRoutesSelectionQuery(), null);

		while (databaseCursor.moveToNext()) {
			ContentValues databaseValues = extractRouteDatabaseValuesFromCursor(databaseCursor);
			routesList.add(new Route(databaseValues));
		}

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

	public Route createRoute(String name) {
		database.beginTransaction();

		try {
			Route route = tryCreateRoute(name);
			database.setTransactionSuccessful();

			return route;
		}
		finally {
			database.endTransaction();
		}
	}

	private Route tryCreateRoute(String name) {
		if (isRouteExist(name)) {
			throw new AlreadyExistsException();
		}

		return getRouteById(insertRoute(name));
	}

	public boolean isRouteExist(String name) {
		Cursor databaseCursor = database.rawQuery(buildRoutesWithNameCountQuery(name), null);
		databaseCursor.moveToFirst();

		final int ROUTES_COUNT_COLUMN_INDEX = 0;
		int routesWithNameCount = databaseCursor.getInt(ROUTES_COUNT_COLUMN_INDEX);

		return routesWithNameCount > 0;
	}

	private String buildRoutesWithNameCountQuery(String name) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select count(*) ");
		queryBuilder.append(String.format("from %s ", DbTableNames.ROUTES));

		queryBuilder.append(String.format("where upper(%s) = upper('%s')", DbFieldNames.NAME, name));

		return queryBuilder.toString();
	}

	private long insertRoute(String name) {
		ContentValues databaseValues = new ContentValues();
		databaseValues.put(DbFieldNames.NAME, name);

		return database.insert(DbTableNames.ROUTES, null, databaseValues);
	}

	private Route getRouteById(long id) {
		Cursor databaseCursor = database.rawQuery(buildRouteByIdSelectionQuery(id), null);
		if (!databaseCursor.moveToFirst()) {
			throw new DbException();
		}

		return new Route(extractRouteDatabaseValuesFromCursor(databaseCursor));
	}

	private String buildRouteByIdSelectionQuery(long id) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");

		queryBuilder.append(String.format("%s, ", DbFieldNames.ID));
		queryBuilder.append(String.format("%s ", DbFieldNames.NAME));

		queryBuilder.append(String.format("from %s ", DbTableNames.ROUTES));
		queryBuilder.append(String.format("where %s = %d", DbFieldNames.ID, id));

		return queryBuilder.toString();
	}
}
