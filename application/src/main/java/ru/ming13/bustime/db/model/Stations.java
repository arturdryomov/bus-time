package ru.ming13.bustime.db.model;


import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.apache.commons.lang3.StringUtils;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.sqlite.DbFieldNames;
import ru.ming13.bustime.db.sqlite.DbTableNames;


public class Stations
{
	private static enum Order
	{
		BY_NAME, BY_TIME_SHIFT
	}

	public Station getStation(long stationId) {
		SQLiteDatabase database = DbProvider.getInstance().getDatabase();
		Cursor databaseCursor = database.rawQuery(buildStationSelectionQuery(stationId), null);

		databaseCursor.moveToFirst();
		Station station = buildStation(databaseCursor);

		databaseCursor.close();

		return station;
	}

	private String buildStationSelectionQuery(long stationId) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");
		queryBuilder.append(String.format("%s, ", DbFieldNames.ID));
		queryBuilder.append(String.format("%s, ", DbFieldNames.NAME));
		queryBuilder.append(String.format("%s, ", DbFieldNames.LATITUDE));
		queryBuilder.append(String.format("%s ", DbFieldNames.LONGITUDE));

		queryBuilder.append(String.format("from %s ", DbTableNames.STATIONS));

		queryBuilder.append(String.format("where %s = %d", DbFieldNames.ID, stationId));

		return queryBuilder.toString();
	}

	private Station buildStation(Cursor databaseCursor) {
		long id = databaseCursor.getLong(databaseCursor.getColumnIndex(DbFieldNames.ID));
		String name = databaseCursor.getString(databaseCursor.getColumnIndex(DbFieldNames.NAME));
		double latitude = databaseCursor.getDouble(
			databaseCursor.getColumnIndex(DbFieldNames.LATITUDE));
		double longitude = databaseCursor.getDouble(
			databaseCursor.getColumnIndex(DbFieldNames.LONGITUDE));

		return new Station(id, name, latitude, longitude);
	}

	public List<Station> getStationsList() {
		return getStationsListForQuery(buildStationsSelectionQuery());
	}

	private String buildStationsSelectionQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");
		queryBuilder.append(String.format("%s, ", DbFieldNames.ID));
		queryBuilder.append(String.format("%s, ", DbFieldNames.NAME));
		queryBuilder.append(String.format("%s, ", DbFieldNames.LATITUDE));
		queryBuilder.append(String.format("%s ", DbFieldNames.LONGITUDE));

		queryBuilder.append(String.format("from %s ", DbTableNames.STATIONS));

		queryBuilder.append(String.format("order by %s", DbFieldNames.NAME));

		return queryBuilder.toString();
	}

	private List<Station> getStationsListForQuery(String stationsSelectionQuery) {
		SQLiteDatabase database = DbProvider.getInstance().getDatabase();
		Cursor databaseCursor = database.rawQuery(stationsSelectionQuery, null);

		List<Station> stationsList = new ArrayList<Station>();

		while (databaseCursor.moveToNext()) {
			stationsList.add(buildStation(databaseCursor));
		}

		databaseCursor.close();

		return stationsList;
	}

	public List<Station> getStationsListOrderedByName(Route route) {
		return getStationsListForQuery(buildStationsSelectionQuery(route, Order.BY_NAME));
	}

	private String buildStationsSelectionQuery(Route route, Order order) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select distinct ");
		queryBuilder.append(
			String.format("%s.%s as %s, ", DbTableNames.STATIONS, DbFieldNames.ID, DbFieldNames.ID));
		queryBuilder.append(
			String.format("%s.%s as %s, ", DbTableNames.STATIONS, DbFieldNames.NAME, DbFieldNames.NAME));
		queryBuilder.append(String.format("%s.%s as %s, ", DbTableNames.STATIONS, DbFieldNames.LATITUDE,
			DbFieldNames.LATITUDE));
		queryBuilder.append(String.format("%s.%s %s ", DbTableNames.STATIONS, DbFieldNames.LONGITUDE,
			DbFieldNames.LONGITUDE));

		queryBuilder.append(String.format("from %s ", DbTableNames.STATIONS));

		queryBuilder.append(String.format("inner join %s ", DbTableNames.ROUTES_AND_STATIONS));
		queryBuilder.append(String.format("on %s.%s = %s.%s ", DbTableNames.STATIONS, DbFieldNames.ID,
			DbTableNames.ROUTES_AND_STATIONS, DbFieldNames.STATION_ID));

		queryBuilder.append(
			String.format("where %s.%s = %d ", DbTableNames.ROUTES_AND_STATIONS, DbFieldNames.ROUTE_ID,
				route.getId()));

		queryBuilder.append(buildStationsOrderQuery(order));

		return queryBuilder.toString();
	}

	private String buildStationsOrderQuery(Order order) {
		switch (order) {
			case BY_NAME:
				return String.format("order by %s.%s", DbTableNames.STATIONS, DbFieldNames.NAME);

			case BY_TIME_SHIFT:
				return String.format("order by %s.%s", DbTableNames.ROUTES_AND_STATIONS,
					DbFieldNames.TIME_SHIFT);

			default:
				return String.format("order by %s.%s", DbTableNames.STATIONS, DbFieldNames.NAME);
		}
	}

	public List<Station> getStationsListOrderedByTimeShift(Route route) {
		return getStationsListForQuery(buildStationsSelectionQuery(route, Order.BY_TIME_SHIFT));
	}

	public List<Station> getStationsList(String searchQuery) {
		List<Station> stationsList = new ArrayList<Station>();

		if (StringUtils.isBlank(searchQuery)) {
			return stationsList;
		}

		searchQuery = buildUniformStationName(searchQuery);

		for (Station station : getStationsList()) {
			String currentStationName = buildUniformStationName(station.getName());

			if (currentStationName.contains(searchQuery)) {
				stationsList.add(station);
			}
		}

		return stationsList;
	}

	private String buildUniformStationName(String stationName) {
		return stationName.toLowerCase().trim();
	}
}
