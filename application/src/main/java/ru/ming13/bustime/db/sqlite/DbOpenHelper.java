package ru.ming13.bustime.db.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbOpenHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "bustime.db";
	private static final int DATABASE_VERSION = 3;

	public DbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldDatabaseVersion, int newDatabaseVersion) {
	}
}
