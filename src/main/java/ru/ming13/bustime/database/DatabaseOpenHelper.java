package ru.ming13.bustime.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseOpenHelper extends SQLiteOpenHelper
{
	private final Context context;

	private SQLiteDatabase database;

	public DatabaseOpenHelper(Context context) {
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
		if (isDatabaseAvailable()) {
			return database;
		}

		if (DatabaseImporter.isDatabaseImportRequired(context)) {
			DatabaseImporter.importDatabase(context);
		}

		database = super.getReadableDatabase();

		return database;
	}

	private boolean isDatabaseAvailable() {
		return (database != null) && (database.isOpen());
	}
}
