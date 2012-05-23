package app.android.bustime.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class DbProvider
{
	private class AlreadyInstantiatedException extends RuntimeException
	{
	}

	private final DbOpenHelper databaseOpenHelper;

	private Routes routes;
	private Stations stations;

	public static DbProvider getInstance() {
		return DbProviderHolder.instance;
	}

	private static class DbProviderHolder
	{
		private static DbProvider instance;

		public DbProviderHolder(Context context) {
			instance = new DbProvider(context);
		}
	}

	private DbProvider(Context context) {
		if (getInstance() != null) {
			throw new AlreadyInstantiatedException();
		}

		databaseOpenHelper = new DbOpenHelper(context.getApplicationContext());
	}

	public static DbProvider getInstance(Context context) {
		if (getInstance() == null) {
			new DbProviderHolder(context);
		}

		return getInstance();
	}

	SQLiteDatabase getDatabase() {
		return databaseOpenHelper.getWritableDatabase();
	}

	public Routes getRoutes() {
		if (routes == null) {
			routes = new Routes(getDatabase());
		}

		return routes;
	}

	public Stations getStations() {
		if (stations == null) {
			stations = new Stations(getDatabase());
		}

		return stations;
	}
}