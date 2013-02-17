package ru.ming13.bustime.db.model;


import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.sqlite.DbFieldNames;
import ru.ming13.bustime.db.sqlite.DbTableNames;


public class Routes
{
	private final SQLiteDatabase database;

	public Routes() {
		database = DbProvider.getInstance().getDatabase();
	}

	public List<Route> getRoutesList() {
		return getRoutesListForQuery(buildRoutesSelectionQuery());
	}

	private String buildRoutesSelectionQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");
		queryBuilder.append(String.format("%s, ", DbFieldNames.ID));
		queryBuilder.append(String.format("%s ", DbFieldNames.NAME));

		queryBuilder.append(String.format("from %s ", DbTableNames.ROUTES));

		queryBuilder.append(String.format("order by cast(%s as integer)", DbFieldNames.NAME));

		return queryBuilder.toString();
	}

	private List<Route> getRoutesListForQuery(String routesSelectionQuery) {
		List<Route> routesList = new ArrayList<Route>();

		Cursor databaseCursor = database.rawQuery(routesSelectionQuery, null);

		while (databaseCursor.moveToNext()) {
			routesList.add(buildRoute(databaseCursor));
		}

		databaseCursor.close();

		return routesList;
	}

	private Route buildRoute(Cursor databaseCursor) {
		long id = databaseCursor.getLong(databaseCursor.getColumnIndex(DbFieldNames.ID));
		String name = databaseCursor.getString(databaseCursor.getColumnIndex(DbFieldNames.NAME));

		return new Route(id, name);
	}

	public List<Route> getRoutesList(Station station) {
		return getRoutesListForQuery(buildRoutesSelectionQuery(station));
	}

	private String buildRoutesSelectionQuery(Station station) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select distinct ");
		queryBuilder.append(
			String.format("%s.%s as %s, ", DbTableNames.ROUTES, DbFieldNames.ID, DbFieldNames.ID));
		queryBuilder.append(
			String.format("%s.%s as %s ", DbTableNames.ROUTES, DbFieldNames.NAME, DbFieldNames.NAME));

		queryBuilder.append(String.format("from %s ", DbTableNames.ROUTES));

		queryBuilder.append(String.format("inner join %s ", DbTableNames.ROUTES_AND_STATIONS));
		queryBuilder.append(String.format("on %s.%s = %s.%s ", DbTableNames.ROUTES, DbFieldNames.ID,
			DbTableNames.ROUTES_AND_STATIONS, DbFieldNames.ROUTE_ID));

		queryBuilder.append(
			String.format("where %s.%s = %d ", DbTableNames.ROUTES_AND_STATIONS, DbFieldNames.STATION_ID,
				station.getId()));

		queryBuilder.append(
			String.format("order by cast(%s.%s as integer)", DbTableNames.ROUTES, DbFieldNames.NAME));

		return queryBuilder.toString();
	}
}
