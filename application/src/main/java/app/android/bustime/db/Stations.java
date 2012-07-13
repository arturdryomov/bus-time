package app.android.bustime.db;


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
		queryBuilder.append(String.format("%s, ", DbFieldNames.NAME));
		queryBuilder.append(String.format("%s, ", DbFieldNames.LATITUDE));
		queryBuilder.append(String.format("%s ", DbFieldNames.LONGITUDE));

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

		double latitude = databaseCursor.getDouble(
			databaseCursor.getColumnIndexOrThrow(DbFieldNames.LATITUDE));
		databaseValues.put(DbFieldNames.LATITUDE, latitude);

		double longitude = databaseCursor.getDouble(
			databaseCursor.getColumnIndexOrThrow(DbFieldNames.LONGITUDE));
		databaseValues.put(DbFieldNames.LONGITUDE, longitude);

		return databaseValues;
	}

	public List<Station> getStationsList(Route route) {
		List<Station> stationsList = new ArrayList<Station>();

		Cursor databaseCursor = database.rawQuery(buildStationsByRouteSelectionQuery(route), null);

		while (databaseCursor.moveToNext()) {
			ContentValues databaseValues = extractStationDatabaseValuesFromCursor(databaseCursor);
			stationsList.add(new Station(databaseValues));
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

		queryBuilder.append(
			String.format("where %s.%s = %d ", DbTableNames.ROUTES_AND_STATIONS, DbFieldNames.ROUTE_ID,
				route.getId()));

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
