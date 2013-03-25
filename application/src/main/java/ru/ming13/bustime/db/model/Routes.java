package ru.ming13.bustime.db.model;


import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.sqlite.DbSchema;


public class Routes
{
	public List<Route> getRoutesList() {
		return getRoutesListForQuery(buildRoutesSelectionQuery());
	}

	private String buildRoutesSelectionQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");
		queryBuilder.append(String.format("%s, ", DbSchema.RoutesColumns._ID));
		queryBuilder.append(String.format("%s ", DbSchema.RoutesColumns.NAME));

		queryBuilder.append(String.format("from %s ", DbSchema.Tables.ROUTES));

		queryBuilder.append(String.format("order by cast(%s as integer)", DbSchema.RoutesColumns.NAME));

		return queryBuilder.toString();
	}

	private List<Route> getRoutesListForQuery(String routesSelectionQuery) {
		SQLiteDatabase database = DbProvider.getInstance().getDatabase();
		Cursor databaseCursor = database.rawQuery(routesSelectionQuery, null);

		List<Route> routesList = new ArrayList<Route>();

		while (databaseCursor.moveToNext()) {
			routesList.add(buildRoute(databaseCursor));
		}

		databaseCursor.close();

		return routesList;
	}

	private Route buildRoute(Cursor databaseCursor) {
		long id = databaseCursor.getLong(databaseCursor.getColumnIndex(DbSchema.RoutesColumns._ID));
		String name = databaseCursor.getString(
			databaseCursor.getColumnIndex(DbSchema.RoutesColumns.NAME));

		return new Route(id, name);
	}

	public List<Route> getRoutesList(Station station) {
		return getRoutesListForQuery(buildRoutesSelectionQuery(station));
	}

	private String buildRoutesSelectionQuery(Station station) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select distinct ");
		queryBuilder.append(
			String.format("%s.%s as %s, ", DbSchema.Tables.ROUTES, DbSchema.RoutesColumns._ID,
				DbSchema.RoutesColumns._ID));
		queryBuilder.append(
			String.format("%s.%s as %s ", DbSchema.Tables.ROUTES, DbSchema.RoutesColumns.NAME,
				DbSchema.RoutesColumns.NAME));

		queryBuilder.append(String.format("from %s ", DbSchema.Tables.ROUTES));

		queryBuilder.append(String.format("inner join %s ", DbSchema.Tables.ROUTES_AND_STATIONS));
		queryBuilder.append(
			String.format("on %s.%s = %s.%s ", DbSchema.Tables.ROUTES, DbSchema.RoutesColumns._ID,
				DbSchema.Tables.ROUTES_AND_STATIONS, DbSchema.RoutesAndStationsColumns.ROUTE_ID));

		queryBuilder.append(String.format("where %s.%s = %d ", DbSchema.Tables.ROUTES_AND_STATIONS,
			DbSchema.RoutesAndStationsColumns.STATION_ID, station.getId()));

		queryBuilder.append(String.format("order by cast(%s.%s as integer)", DbSchema.Tables.ROUTES,
			DbSchema.RoutesColumns.NAME));

		return queryBuilder.toString();
	}
}
