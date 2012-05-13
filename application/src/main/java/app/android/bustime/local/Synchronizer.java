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

	private SQLiteDatabase localDatabase;
	private SQLiteDatabase remoteDatabase;

	public Synchronizer() {
		localDatabase = DbProvider.getInstance().getDatabase();
	}

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

	public void importDatabase(String importDatabasePath) {
		if (!isRemoteDatabaseCorrect(importDatabasePath)) {
			throw new SyncException();
		}

		File localDatabaseFile = new File(getLocalDatabasePath());
		File importDatabaseFile = new File(importDatabasePath);

		copyFile(importDatabaseFile, localDatabaseFile);
	}

	public boolean isRemoteDatabaseCorrect(String remoteDatabasePath) {
		remoteDatabase = SQLiteDatabase.openDatabase(remoteDatabasePath, null,
			SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

		return areRemoteDatabaseTablesCorrect() && areRemoteDatabaseColumnsCorrect();
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
}
