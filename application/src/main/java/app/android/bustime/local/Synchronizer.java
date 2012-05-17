package app.android.bustime.local;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Synchronizer
{
	private static final int DATABASE_TABLE_NAME_COLUMN_INDEX = 0;
	private static final int DATABASE_TABLE_COLUMN_NAME_COLUMN_INDEX = 1;

	private final SQLiteDatabase localDatabase;
	private SQLiteDatabase remoteDatabase;

	private Stations localStations;
	private Stations remoteStations;

	private Routes localRoutes;
	private Routes remoteRoutes;

	public Synchronizer() {
		localDatabase = DbProvider.getInstance().getDatabase();
	}

	/**
	 * @throws SyncException if something went wrong during copying of database file.
	 */
	public void exportDatabase(String exportDatabasePath) {
		File localDatabaseFile = new File(getLocalDatabasePath());
		File exportDatabaseFile = new File(exportDatabasePath);

		copyFile(localDatabaseFile, exportDatabaseFile);
	}

	private String getLocalDatabasePath() {
		return localDatabase.getPath();
	}

	private void copyFile(File sourceFile, File destinationFile) {
		FileChannel sourceChannel;
		FileChannel destinationChannel;

		try {
			sourceChannel = new FileInputStream(sourceFile).getChannel();
			destinationChannel = new FileOutputStream(destinationFile).getChannel();
		}
		catch (FileNotFoundException e) {
			throw new SyncException();
		}

		try {
			sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
			sourceChannel.close();
			destinationChannel.close();
		}
		catch (IOException e) {
			throw new SyncException();
		}
	}

	/**
	 * @throws SyncException if something went wrong during copying of database file or database is
	 * not valid.
	 */
	public void importDatabase(String importDatabasePath) {
		if (!isRemoteDatabaseCorrect(importDatabasePath)) {
			throw new SyncException();
		}

		File localDatabaseFile = new File(getLocalDatabasePath());
		File importDatabaseFile = new File(importDatabasePath);

		copyFile(importDatabaseFile, localDatabaseFile);
	}

	private boolean isRemoteDatabaseCorrect(String remoteDatabasePath) {
		remoteDatabase = SQLiteDatabase.openDatabase(remoteDatabasePath, null,
			SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

		boolean isRemoteDatabaseCorrect = areRemoteDatabaseTablesCorrect() && areRemoteDatabaseColumnsCorrect();

		remoteDatabase.close();

		return isRemoteDatabaseCorrect;
	}

	private boolean areRemoteDatabaseTablesCorrect() {
		return getRemoteDatabaseTableNames().containsAll(getLocalDatabaseTableNames());
	}

	private Set<String> getRemoteDatabaseTableNames() {
		Set<String> databaseTableNames = new HashSet<String>();

		Cursor databaseCursor = remoteDatabase.rawQuery(buildTablesNamesQuery(), null);

		while (databaseCursor.moveToNext()) {
			String databaseTableName = databaseCursor.getString(DATABASE_TABLE_NAME_COLUMN_INDEX);
			databaseTableNames.add(databaseTableName);
		}

		databaseCursor.close();

		return databaseTableNames;
	}

	private String buildTablesNamesQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select name ");
		queryBuilder.append("from sqlite_master ");
		queryBuilder.append("where type = 'table'");

		return queryBuilder.toString();
	}

	private Set<String> getLocalDatabaseTableNames() {
		Set<String> localDatabaseTableNames = new HashSet<String>();

		localDatabaseTableNames.add(DbTableNames.ROUTES);
		localDatabaseTableNames.add(DbTableNames.TRIPS);
		localDatabaseTableNames.add(DbTableNames.STATIONS);
		localDatabaseTableNames.add(DbTableNames.ROUTES_AND_STATIONS);

		return localDatabaseTableNames;
	}

	private boolean areRemoteDatabaseColumnsCorrect() {
		for (String tableName : getLocalDatabaseTableNames()) {
			Set<String> localDatabaseColumnNames = getTableColumnNames(localDatabase, tableName);
			Set<String> remoteDatabaseColumnNames = getTableColumnNames(remoteDatabase, tableName);

			if (!remoteDatabaseColumnNames.containsAll(localDatabaseColumnNames)) {
				return false;
			}
		}

		return true;
	}

	private Set<String> getTableColumnNames(SQLiteDatabase database, String tableName) {
		Set<String> tableColumnNames = new HashSet<String>();

		Cursor databaseCursor = database.rawQuery(buildTableColumnsInformationQuery(tableName), null);

		while (databaseCursor.moveToNext()) {
			String tableColumnName = databaseCursor.getString(DATABASE_TABLE_COLUMN_NAME_COLUMN_INDEX);
			tableColumnNames.add(tableColumnName);
		}

		databaseCursor.close();

		return tableColumnNames;
	}

	private String buildTableColumnsInformationQuery(String tableName) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("pragma ");
		queryBuilder.append(String.format("table_info(%s)", tableName));

		return queryBuilder.toString();
	}

	/**
	 * @throws SyncException if database is not valid.
	 */
	public void importDatabase(String importDatabasePath, boolean isUpdatingEnabled) {
		if (!isRemoteDatabaseCorrect(importDatabasePath)) {
			throw new SyncException();
		}

		remoteDatabase = SQLiteDatabase.openDatabase(importDatabasePath, null,
			SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

		localStations = DbProvider.getInstance().getStations();
		remoteStations = new Stations(remoteDatabase);

		localRoutes = DbProvider.getInstance().getRoutes();
		remoteRoutes = new Routes(remoteDatabase);

		importStations(isUpdatingEnabled);
		importRoutes(isUpdatingEnabled);

		remoteDatabase.close();
	}

	private void importStations(boolean isUpdatingEnabled) {
		for (Station remoteStation : remoteStations.getStationsList()) {
			try {
				Station localStation = getStation(localStations, remoteStation.getName());

				if (isUpdatingEnabled) {
					localStation.setLocation(remoteStation.getLatitude(), remoteStation.getLongitude());
				}
			}
			catch (NotExistsException e) {
				localStations.createStation(remoteStation.getName(), remoteStation.getLatitude(),
					remoteStation.getLongitude());
			}
		}
	}

	private Station getStation(Stations stations, String stationName) {
		for (Station station : stations.getStationsList()) {
			String existingStationName = unifyName(station.getName());
			String searchStringName = unifyName(stationName);

			if (existingStationName.equals(searchStringName)) {
				return station;
			}
		}

		throw new NotExistsException();
	}

	private String unifyName(String name) {
		return name.toUpperCase().trim();
	}

	private void importRoutes(boolean isUpdatingEnabled) {
		for (Route remoteRoute : remoteRoutes.getRoutesList()) {
			Route localRoute;

			try {
				localRoute = getRoute(localRoutes, remoteRoute.getName());

				if (!isUpdatingEnabled) {
					continue;
				}
			}
			catch (NotExistsException e) {
				localRoute = localRoutes.createRoute(remoteRoute.getName());
			}

			importDepartureTimetable(remoteRoute, localRoute);
			importStations(remoteRoute, remoteStations, localRoute, localStations);
		}
	}

	private Route getRoute(Routes routes, String routeName) {
		for (Route route : routes.getRoutesList()) {
			String existingRouteName = unifyName(route.getName());
			String searchRouteName = unifyName(routeName);

			if (existingRouteName.equals(searchRouteName)) {
				return route;
			}
		}

		throw new NotExistsException();
	}

	private void importDepartureTimetable(Route sourceRoute, Route destinationRoute) {
		for (Time departureTime : sourceRoute.getDepartureTimetable()) {
			try {
				destinationRoute.insertDepartureTime(departureTime);
			}
			catch (AlreadyExistsException e) {
				// Just ignore existing departure times
			}
		}
	}

	private void importStations(Route sourceRoute, Stations sourceStations, Route destinationRoute, Stations destinationStations) {
		for (Station sourceStation : sourceStations.getStationsList(sourceRoute)) {
			Station destinationStation = getStation(destinationStations, sourceStation.getName());

			Time sourceStationShiftTime = sourceStation.getShiftTimeForRoute(sourceRoute);

			try {
				Time destinationStationShiftTime = destinationStation.getShiftTimeForRoute(
					destinationRoute);

				destinationStation.removeShiftTimeForRoute(destinationRoute);

				try {
					destinationStation.insertShiftTimeForRoute(destinationRoute, sourceStationShiftTime);
				}
				catch (AlreadyExistsException e) {
					destinationStation.insertShiftTimeForRoute(destinationRoute, destinationStationShiftTime);
				}
			}
			catch (NotExistsException e) {
				destinationStation.insertShiftTimeForRoute(destinationRoute, sourceStationShiftTime);
			}
		}
	}
}
