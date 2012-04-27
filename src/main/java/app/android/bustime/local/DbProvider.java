package app.android.bustime.local;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class DbProvider
{
	public class AlreadyInstantiatedException extends RuntimeException
	{
	}

	private static DbProvider instance;
	private final DbOpenHelper dbOpenHelper;

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

		dbOpenHelper = new DbOpenHelper(context.getApplicationContext());

		instance = this;
	}

	SQLiteDatabase getDatabase() {
		return dbOpenHelper.getWritableDatabase();
	}
}
