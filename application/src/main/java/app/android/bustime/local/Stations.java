package app.android.bustime.local;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Stations
{
	private final SQLiteDatabase database;

	Stations() {
		database = DbProvider.getInstance().getDatabase();
	}

	public List<Station> getStationsList() {
		List<Station> stationsList = new ArrayList<Station>();

		Cursor databaseCursor = database.rawQuery(buildStationsSelectionQuery(), null);

		while (databaseCursor.moveToNext()) {
			ContentValues databaseValues = extractStationDatabaseValuesFromCursor(databaseCursor);
			stationsList.add(new Station(databaseValues));
		}

		databaseCursor.close();

		return stationsList;
	}

	private String buildStationsSelectionQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");

		queryBuilder.append(String.format("%s, ", DbFieldNames.ID));
		queryBuilder.append(String.format("%s ", DbFieldNames.NAME));

		queryBuilder.append(String.format("from %s ", DbTableNames.STATIONS));
		queryBuilder.append(String.format("order by %s", DbFieldNames.NAME));

		return queryBuilder.toString();
	}

	private ContentValues extractStationDatabaseValuesFromCursor(Cursor databaseCursor) {
		ContentValues databaseValues = new ContentValues();

		long id = databaseCursor.getLong(databaseCursor.getColumnIndexOrThrow(DbFieldNames.ID));
		databaseValues.put(DbFieldNames.ID, id);

		String name = databaseCursor.getString(databaseCursor.getColumnIndexOrThrow(DbFieldNames.NAME));
		databaseValues.put(DbFieldNames.NAME, name);

		return databaseValues;
	}

	public Station createStation(String name) {
		database.beginTransaction();

		try {
			Station station = tryCreateStation(name);

			database.setTransactionSuccessful();
			return station;
		}
		finally {
			database.endTransaction();
		}
	}

	private Station tryCreateStation(String name) {
		if (isStationExist(name)) {
			throw new AlreadyExistsException();
		}

		return getStationById(insertStation(name));
	}

	boolean isStationExist(String name) {
		Cursor databaseCursor = database.rawQuery(buildStationsWithNameCountQuery(name), null);
		databaseCursor.moveToFirst();

		final int STATIONS_COUNT_COLUMN_INDEX = 0;
		int stationsWithNameCount = databaseCursor.getInt(STATIONS_COUNT_COLUMN_INDEX);

		boolean isStationExist = stationsWithNameCount > 0;

		databaseCursor.close();

		return isStationExist;
	}

	private String buildStationsWithNameCountQuery(String name) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select count(*)");
		queryBuilder.append(String.format("from %s ", DbTableNames.STATIONS));
		queryBuilder.append(String.format("where upper(%s) = upper('%s')", DbFieldNames.NAME, name));

		return queryBuilder.toString();
	}

	private long insertStation(String name) {
		ContentValues databaseValues = new ContentValues();
		databaseValues.put(DbFieldNames.NAME, name);

		return database.insert(DbTableNames.STATIONS, null, databaseValues);
	}

	private Station getStationById(long id) {
		Cursor databaseCursor = database.rawQuery(buildStationByIdSelectionQuery(id), null);
		if (!databaseCursor.moveToFirst()) {
			throw new DbException();
		}

		Station createdStation = new Station(extractStationDatabaseValuesFromCursor(databaseCursor));

		databaseCursor.close();

		return createdStation;
	}

	private String buildStationByIdSelectionQuery(long id) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");

		queryBuilder.append(String.format("%s, ", DbFieldNames.ID));
		queryBuilder.append(String.format("%s ", DbFieldNames.NAME));

		queryBuilder.append(String.format("from %s ", DbTableNames.STATIONS));
		queryBuilder.append(String.format("where %s = %d", DbFieldNames.ID, id));

		return queryBuilder.toString();
	}

	public void deleteStation(Station station) {
		database.beginTransaction();

		try {
			tryDeleteStation(station);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void tryDeleteStation(Station station) {
		database.delete(DbTableNames.ROUTES_AND_STATIONS,
			String.format("%s = %d", DbFieldNames.STATION_ID, station.getId()), null);

		database.delete(DbTableNames.STATIONS,
			String.format("%s = %d", DbFieldNames.ID, station.getId()), null);
	}

	public List<Station> getStationsByRoute(Route route) {
		List<Station> stationsList = new ArrayList<Station>();

		Cursor databaseCursor = database.rawQuery(buildStationsByRouteSelectionQuery(route), null);

		while (databaseCursor.moveToNext()) {
			ContentValues databaseValues = extractStationDatabaseValuesFromCursor(databaseCursor);
			stationsList.add(new Station(databaseValues));
		}

		return stationsList;
	}

	private String buildStationsByRouteSelectionQuery(Route route) {
		StringBuilder queryBulder = new StringBuilder();

		queryBulder.append("select distinct ");

		queryBulder.append(String.format("%s.%s, ", DbTableNames.STATIONS, DbFieldNames.ID));
		queryBulder.append(String.format("%s.%s ", DbTableNames.STATIONS, DbFieldNames.NAME));

		queryBulder.append(String.format("from %s ", DbTableNames.STATIONS));
		queryBulder.append(String.format("inner join %s ", DbTableNames.ROUTES_AND_STATIONS));
		queryBulder.append(String.format("on %s.%s = %s.%s ", DbTableNames.STATIONS, DbFieldNames.ID,
			DbTableNames.ROUTES_AND_STATIONS, DbFieldNames.STATION_ID));

		queryBulder.append(String.format("where %s.%s = %d", DbTableNames.ROUTES_AND_STATIONS,
			DbFieldNames.ROUTE_ID, route.getId()));

		return queryBulder.toString();
	}

	public void beginTransaction() {
		database.beginTransaction();
	}

	public void endTransaction() {
		database.endTransaction();
	}
}