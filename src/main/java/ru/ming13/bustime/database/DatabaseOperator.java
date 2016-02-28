package ru.ming13.bustime.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ru.ming13.bustime.util.Files;
import ru.ming13.bustime.util.SqlBuilder;

public final class DatabaseOperator
{
	private final Context context;

	@NonNull
	public static DatabaseOperator with(@NonNull Context context) {
		return new DatabaseOperator(context);
	}

	private DatabaseOperator(Context context) {
		this.context = context.getApplicationContext();
	}

	public boolean databaseExists() {
		return getDatabaseFile().exists();
	}

	private File getDatabaseFile() {
		return context.getDatabasePath(DatabaseSchema.DATABASE_NAME).getAbsoluteFile();
	}

	@NonNull
	public String getDatabasePath() {
		return getDatabaseFile().getPath();
	}

	public void replaceDatabaseFile(@NonNull InputStream databaseContents) {
		try {
			File tempDatabaseFile = getTempFile(databaseContents);
			File databaseFile = getDatabaseFile();

			Files.copy(tempDatabaseFile, databaseFile);

			tempDatabaseFile.delete();
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
	}

	private File getTempFile(InputStream fileContents) {
		try {
			File tempFile = File.createTempFile("bustime", null, context.getCacheDir());

			Files.copy(fileContents, tempFile);

			return tempFile;
		} catch (RuntimeException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void replaceDatabaseContents(@NonNull InputStream databaseContents) {
		SQLiteDatabase database = new DatabaseOpenHelper(context).getWritableDatabase();
		File tempDatabaseFile = getTempFile(databaseContents);

		deleteDatabaseContents(database);
		insertDatabaseContents(database, tempDatabaseFile);

		database.close();
		tempDatabaseFile.delete();
	}

	private void deleteDatabaseContents(SQLiteDatabase database) {
		try {
			database.beginTransaction();

			database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.ROUTES_AND_STOPS));
			database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.TRIPS));
			database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.TRIP_TYPES));
			database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.ROUTES));
			database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.STOPS));

			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}

	private void insertDatabaseContents(SQLiteDatabase database, File databaseContentsFile) {
		database.execSQL(SqlBuilder.buildAttachClause(databaseContentsFile.getAbsolutePath(), DatabaseSchema.Aliases.DATABASE));

		try {
			database.beginTransaction();

			database.execSQL(SqlBuilder.buildInsertClause(DatabaseSchema.Tables.ROUTES, DatabaseSchema.Aliases.DATABASE));
			database.execSQL(SqlBuilder.buildInsertClause(DatabaseSchema.Tables.TRIP_TYPES, DatabaseSchema.Aliases.DATABASE));
			database.execSQL(SqlBuilder.buildInsertClause(DatabaseSchema.Tables.TRIPS, DatabaseSchema.Aliases.DATABASE));
			database.execSQL(SqlBuilder.buildInsertClause(DatabaseSchema.Tables.STOPS, DatabaseSchema.Aliases.DATABASE));
			database.execSQL(SqlBuilder.buildInsertClause(DatabaseSchema.Tables.ROUTES_AND_STOPS, DatabaseSchema.Aliases.DATABASE));

			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}

		database.execSQL(SqlBuilder.buildDetachClause(DatabaseSchema.Aliases.DATABASE));
	}
}
