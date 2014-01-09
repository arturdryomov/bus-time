package ru.ming13.bustime.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ru.ming13.bustime.util.SqlBuilder;


public final class DatabaseOperator
{
	private final Context context;

	public static DatabaseOperator with(Context context) {
		return new DatabaseOperator(context);
	}

	private DatabaseOperator(Context context) {
		this.context = context.getApplicationContext();
	}

	public boolean doesDatabaseExist() {
		return buildDatabaseFile().exists();
	}

	private File buildDatabaseFile() {
		return context.getDatabasePath(DatabaseSchema.DATABASE_NAME).getAbsoluteFile();
	}

	public void replaceDatabaseFile(InputStream databaseContent) {
		try {
			File tempDatabaseFile = buildTempFile(databaseContent);
			File databaseFile = buildDatabaseFile();

			FileUtils.moveFile(tempDatabaseFile, databaseFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File buildTempFile(InputStream fileContent) {
		try {
			File tempFile = File.createTempFile("bustime", null, context.getCacheDir());
			FileUtils.copyInputStreamToFile(fileContent, tempFile);
			return tempFile;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (NullPointerException e) {
			throw new RuntimeException(e);
		}
	}

	public void replaceDatabaseContent(InputStream databaseContent) {
		SQLiteDatabase database = new DatabaseOpenHelper(context).getReadableDatabase();
		File tempDatabaseFile = buildTempFile(databaseContent);

		deleteDatabaseContent(database);
		insertDatabaseContent(database, tempDatabaseFile);

		tempDatabaseFile.delete();
	}

	private void deleteDatabaseContent(SQLiteDatabase database) {
		try {
			database.beginTransaction();

			database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.ROUTES));
			database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.TRIP_TYPES));
			database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.TRIPS));
			database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.STATIONS));
			database.execSQL(SqlBuilder.buildDeleteClause(DatabaseSchema.Tables.ROUTES_AND_STATIONS));

			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}

	private void insertDatabaseContent(SQLiteDatabase database, File databaseContentFile) {
		database.execSQL(SqlBuilder.buildAttachClause(databaseContentFile.getAbsolutePath(), "db"));

		try {
			database.beginTransaction();

			database.execSQL(SqlBuilder.buildInsertClause(DatabaseSchema.Tables.ROUTES, "db"));
			database.execSQL(SqlBuilder.buildInsertClause(DatabaseSchema.Tables.TRIP_TYPES, "db"));
			database.execSQL(SqlBuilder.buildInsertClause(DatabaseSchema.Tables.TRIPS, "db"));
			database.execSQL(SqlBuilder.buildInsertClause(DatabaseSchema.Tables.STATIONS, "db"));
			database.execSQL(SqlBuilder.buildInsertClause(DatabaseSchema.Tables.ROUTES_AND_STATIONS, "db"));

			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}

		database.execSQL(SqlBuilder.buildDetachClause("db"));
	}
}
