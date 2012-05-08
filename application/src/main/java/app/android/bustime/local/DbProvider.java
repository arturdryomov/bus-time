package app.android.bustime.local;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class DbProvider
{
	public class AlreadyInstantiatedException extends RuntimeException
	{
	}

	private static DbProvider instance;

	private final DbOpenHelper databaseOpenHelper;
	private final SQLiteDatabase writeableDatabase;

	private Routes routes;
	private Stations stations;

	public static DbProvider getInstance() {
		return instance;
	}

	public static DbProvider getInstance(Context context) {
		if (instance == null) {
			return new DbProvider(context);
		}
		else {
			return instance;
		}
	}

	private DbProvider(Context context) {
		if (instance != null) {
			throw new AlreadyInstantiatedException();
		}

		databaseOpenHelper = new DbOpenHelper(context.getApplicationContext());
		writeableDatabase = databaseOpenHelper.getWritableDatabase();

		instance = this;
	}

	SQLiteDatabase getDatabase() {
		return writeableDatabase;
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
