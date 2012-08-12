package ru.ming13.bustime.db.model;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.sqlite.DbFieldNames;
import ru.ming13.bustime.db.sqlite.DbTableNames;


public class Stations
{
	private final SQLiteDatabase database;

	public Stations() {
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

		DatabaseUtils.cursorLongToContentValues(databaseCursor, DbFieldNames.ID, databaseValues);
		DatabaseUtils.cursorStringToContentValues(databaseCursor, DbFieldNames.NAME, databaseValues);
		DatabaseUtils.cursorDoubleToContentValues(databaseCursor, DbFieldNames.LATITUDE, databaseValues,
			DbFieldNames.LATITUDE);
		DatabaseUtils.cursorDoubleToContentValues(databaseCursor, DbFieldNames.LONGITUDE,
			databaseValues, DbFieldNames.LONGITUDE);

		return databaseValues;
	}

	public List<Station> getStationsList(Route route) {
		List<Station> stationsList = new ArrayList<Station>();

		Cursor databaseCursor = database.rawQuery(buildRoutesStationsSelectionQuery(route), null);

		while (databaseCursor.moveToNext()) {
			ContentValues databaseValues = extractStationDatabaseValuesFromCursor(databaseCursor);
			stationsList.add(new Station(databaseValues));
		}

		return stationsList;
	}

	private String buildRoutesStationsSelectionQuery(Route route) {
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

		queryBuilder.append(String.format("order by %s.%s", DbTableNames.STATIONS, DbFieldNames.NAME));

		return queryBuilder.toString();
	}

	public List<Station> getStationsList(String searchQuery) {
		List<Station> stationsList = new ArrayList<Station>();

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
