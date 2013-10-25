package ru.ming13.bustime.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseOpenHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "bustime.db";
	private static final int DATABASE_VERSION = 4;

	private final Context context;

	public DatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

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
		if (DatabaseImporter.isDatabaseImportRequired(context)) {
			DatabaseImporter.importDatabase(context);
		}

		return super.getReadableDatabase();
	}
}
