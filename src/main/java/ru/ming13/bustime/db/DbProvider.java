package ru.ming13.bustime.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import ru.ming13.bustime.db.content.DbImporter;
import ru.ming13.bustime.db.model.Routes;
import ru.ming13.bustime.db.model.Stations;
import ru.ming13.bustime.db.sqlite.DbOpenHelper;


public class DbProvider
{
	private DbOpenHelper databaseOpenHelper;

	private Routes routes;
	private Stations stations;

	private static DbProvider INSTANCE;

	public static void setUp(Context context) {
		INSTANCE = new DbProvider(context.getApplicationContext());
	}

	public static DbProvider getInstance() {
		return INSTANCE;
	}

	private DbProvider(Context context) {
		importDatabaseIfNecessary(context);

		databaseOpenHelper = new DbOpenHelper(context);
	}

	private void importDatabaseIfNecessary(Context context) {
		DbImporter dbImporter = new DbImporter(context);

		if (!dbImporter.isLocalDatabaseExist()) {
			dbImporter.importFromAssets();
		}
	}

	public void refreshDatabase(Context context) {
		databaseOpenHelper.close();
		databaseOpenHelper = new DbOpenHelper(context.getApplicationContext());

		routes = new Routes();
		stations = new Stations();
	}

	public SQLiteDatabase getDatabase() {
		return databaseOpenHelper.getReadableDatabase();
	}

	public Routes getRoutes() {
		if (routes == null) {
			routes = new Routes();
		}

		return routes;
	}

	public Stations getStations() {
		if (stations == null) {
			stations = new Stations();
		}

		return stations;
	}
}
