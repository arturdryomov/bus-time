package app.android.bustime.local;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;


public class Stations
{
	private final static int CLOSE_DISTANCE_IN_METERS = 500;

	private final SQLiteDatabase database;

	Stations(SQLiteDatabase database) {
		this.database = database;
	}

	/**
	 * @throws AlreadyExistsException if station with such name already exists.
	 * @throws DbException if something internal went wrong during creating.
	 */
	public Station createStation(String name, double latitude, double longitude) {
		database.beginTransaction();

		try {
			Station station = tryCreateStation(name, latitude, longitude);

			database.setTransactionSuccessful();
			return station;
		}
		finally {
			database.endTransaction();
		}
	}

	private Station tryCreateStation(String name, double latitude, double longitude) {
		if (isStationExist(name)) {
			throw new AlreadyExistsException();
		}

		return getStationById(insertStation(name, latitude, longitude));
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

	private long insertStation(String name, double latitude, double longitude) {
		ContentValues databaseValues = new ContentValues();
		databaseValues.put(DbFieldNames.NAME, name);
		databaseValues.put(DbFieldNames.LATITUDE, latitude);
		databaseValues.put(DbFieldNames.LONGITUDE, longitude);

		return database.insert(DbTableNames.STATIONS, null, databaseValues);
	}

	private Station getStationById(long id) {
		Cursor databaseCursor = database.rawQuery(buildStationByIdSelectionQuery(id), null);
		if (!databaseCursor.moveToFirst()) {
			throw new DbException();
		}

		Station createdStation = new Station(database, this, extractStationDatabaseValuesFromCursor
			(databaseCursor));

		databaseCursor.close();

		return createdStation;
	}

	private String buildStationByIdSelectionQuery(long id) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");

		queryBuilder.append(String.format("%s, ", DbFieldNames.ID));
		queryBuilder.append(String.format("%s, ", DbFieldNames.NAME));
		queryBuilder.append(String.format("%s, ", DbFieldNames.LATITUDE));
		queryBuilder.append(String.format("%s ", DbFieldNames.LONGITUDE));

		queryBuilder.append(String.format("from %s ", DbTableNames.STATIONS));
		queryBuilder.append(String.format("where %s = %d", DbFieldNames.ID, id));

		return queryBuilder.toString();
	}

	private ContentValues extractStationDatabaseValuesFromCursor(Cursor databaseCursor) {
		ContentValues databaseValues = new ContentValues();

		long id = databaseCursor.getLong(databaseCursor.getColumnIndexOrThrow(DbFieldNames.ID));
		databaseValues.put(DbFieldNames.ID, id);

		String name = databaseCursor.getString(databaseCursor.getColumnIndexOrThrow(DbFieldNames.NAME));
		databaseValues.put(DbFieldNames.NAME, name);

		double latitude = databaseCursor.getDouble(databaseCursor
			.getColumnIndexOrThrow(DbFieldNames.LATITUDE));
		databaseValues.put(DbFieldNames.LATITUDE, latitude);

		double longitude = databaseCursor.getDouble(databaseCursor
			.getColumnIndexOrThrow(DbFieldNames.LONGITUDE));
		databaseValues.put(DbFieldNames.LONGITUDE, longitude);

		return databaseValues;
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

	public List<Station> getStationsList() {
		List<Station> stationsList = new ArrayList<Station>();

		Cursor databaseCursor = database.rawQuery(buildStationsSelectionQuery(), null);

		while (databaseCursor.moveToNext()) {
			ContentValues databaseValues = extractStationDatabaseValuesFromCursor(databaseCursor);
			stationsList.add(new Station(database, this, databaseValues));
		}

		databaseCursor.close();

		return stationsList;
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

	public List<Station> getStationsList(Route route) {
		List<Station> stationsList = new ArrayList<Station>();

		Cursor databaseCursor = database.rawQuery(buildStationsByRouteSelectionQuery(route), null);

		while (databaseCursor.moveToNext()) {
			ContentValues databaseValues = extractStationDatabaseValuesFromCursor(databaseCursor);
			stationsList.add(new Station(database, this, databaseValues));
		}

		return stationsList;
	}

	private String buildStationsByRouteSelectionQuery(Route route) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select distinct ");

		queryBuilder.append(String.format("%s.%s, ", DbTableNames.STATIONS, DbFieldNames.ID));
		queryBuilder.append(String.format("%s.%s, ", DbTableNames.STATIONS, DbFieldNames.NAME));
		queryBuilder.append(String.format("%s.%s, ", DbTableNames.STATIONS, DbFieldNames.LATITUDE));
		queryBuilder.append(String.format("%s.%s ", DbTableNames.STATIONS, DbFieldNames.LONGITUDE));

		queryBuilder.append(String.format("from %s ", DbTableNames.STATIONS));
		queryBuilder.append(String.format("inner join %s ", DbTableNames.ROUTES_AND_STATIONS));
		queryBuilder.append(String.format("on %s.%s = %s.%s ", DbTableNames.STATIONS, DbFieldNames.ID,
			DbTableNames.ROUTES_AND_STATIONS, DbFieldNames.STATION_ID));

		queryBuilder.append(String.format("where %s.%s = %d ", DbTableNames.ROUTES_AND_STATIONS,
			DbFieldNames.ROUTE_ID, route.getId()));

		queryBuilder.append(String.format("order by %s.%s", DbTableNames.STATIONS, DbFieldNames.NAME));

		return queryBuilder.toString();
	}

	public List<Station> getStationsList(double latitude, double longitude) {
		return getClosestStations(latitude, longitude, getStationsList());
	}

	private List<Station> getClosestStations(double latitude, double longitude, List<Station> stations) {
		List<Station> closestStations = new ArrayList<Station>();

		Location currentLocation = constructLocation(latitude, longitude);

		for (Station station : stations) {
			Location stationLocation = constructLocation(station.getLatitude(), station.getLongitude());

			if (currentLocation.distanceTo(stationLocation) <= CLOSE_DISTANCE_IN_METERS) {
				closestStations.add(station);
			}
		}

		return closestStations;
	}

	private Location constructLocation(double latitude, double longitude) {
		Location location = new Location(new String());
		location.setLatitude(latitude);
		location.setLongitude(longitude);

		return location;
	}

	public List<Station> getStationsList(Route route, double latitude, double longitude) {
		return getClosestStations(latitude, longitude, getStationsList(route));
	}

	public void beginTransaction() {
		database.beginTransaction();
	}

	public void endTransaction() {
		database.endTransaction();
	}
}
