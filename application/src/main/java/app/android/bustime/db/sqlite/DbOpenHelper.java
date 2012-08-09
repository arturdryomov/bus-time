package app.android.bustime.db.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import app.android.bustime.db.content.DbImporter;


public class DbOpenHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "bustime.db";
	private static final int DATABASE_VERSION = 3;

	private final Context context;

	public DbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldDatabaseVersion, int newDatabaseVersion) {
	}

	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		DbImporter dbImporter = new DbImporter(context);

		if (!dbImporter.isLocalDatabaseExist()) {
			dbImporter.importFromAssets();
		}

		return super.getWritableDatabase();
	}
}
