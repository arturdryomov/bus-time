package ru.ming13.bustime.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import ru.ming13.bustime.util.Assets;

public class DatabaseOpenHelper extends SQLiteOpenHelper
{
	private final Context context;

	private SQLiteDatabase database;

	public DatabaseOpenHelper(@NonNull Context context) {
		super(context, DatabaseSchema.DATABASE_NAME, null, DatabaseSchema.Versions.CURRENT);

		this.context = context.getApplicationContext();
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldDatabaseVersion, int newDatabaseVersion) {
	}

	@Override
	public SQLiteDatabase getReadableDatabase() {
		if (databaseAvailable()) {
			return database;
		}

		if (!databaseExists()) {
			createDatabase();
			changeDatabaseVersion();
		}

		if (!databaseHasCurrentVersion()) {
			createDatabase();
			changeDatabaseVersion();
		}

		database = openReadableDatabase();

		return database;
	}

	private boolean databaseAvailable() {
		return (database != null) && (database.isOpen());
	}

	private boolean databaseExists() {
		return DatabaseOperator.with(context).databaseExists();
	}

	private void createDatabase() {
		DatabaseOperator.with(context).replaceDatabaseFile(Assets.of(context).getDatabaseContents());
	}

	private void changeDatabaseVersion() {
		SQLiteDatabase database = openWriteableDatabase();
		database.setVersion(DatabaseSchema.Versions.CURRENT);
		database.close();
	}

	private SQLiteDatabase openWriteableDatabase() {
		return SQLiteDatabase.openDatabase(getDatabasePath(), null, SQLiteDatabase.OPEN_READWRITE);
	}

	private String getDatabasePath() {
		return DatabaseOperator.with(context).getDatabasePath();
	}

	private boolean databaseHasCurrentVersion() {
		SQLiteDatabase database = openReadableDatabase();
		int databaseVersion = database.getVersion();
		database.close();

		return databaseVersion == DatabaseSchema.Versions.CURRENT;
	}

	private SQLiteDatabase openReadableDatabase() {
		return SQLiteDatabase.openDatabase(getDatabasePath(), null, SQLiteDatabase.OPEN_READONLY);
	}
}
