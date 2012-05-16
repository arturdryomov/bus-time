package app.android.bustime.local;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class DbOpenHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "bustime.db";

	public DbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();

		try {
			createTables(db);

			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
	}

	private void createTables(SQLiteDatabase db) {
		db.execSQL(buildRoutesTableCreationQuery());
		db.execSQL(buildTripsTableCreationQuery());
		db.execSQL(buildStationsTableCreationQuery());
		db.execSQL(buildRoutesAndStationsTableCreationQuery());
	}

	private String buildRoutesTableCreationQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("create table if not exists %s ", DbTableNames.ROUTES));

		queryBuilder.append("(");
		queryBuilder.append(String.format("%s %s, ", DbFieldNames.ID, DbFieldParameters.ID));
		queryBuilder.append(String.format("%s %s", DbFieldNames.NAME, DbFieldParameters.NAME));
		queryBuilder.append(")");

		return queryBuilder.toString();
	}

	private String buildTripsTableCreationQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("create table if not exists %s ", DbTableNames.TRIPS));

		queryBuilder.append("(");
		queryBuilder.append(String.format("%s %s, ", DbFieldNames.ID, DbFieldParameters.ID));
		queryBuilder.append(String.format("%s %s, ", DbFieldNames.ROUTE_ID,
			DbFieldParameters.FOREIGN_ROUTE_ID));
		queryBuilder
			.append(String.format("%s %s", DbFieldNames.DEPARTURE_TIME, DbFieldParameters.TIME));
		queryBuilder.append(")");

		return queryBuilder.toString();
	}

	private String buildStationsTableCreationQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("create table if not exists %s ", DbTableNames.STATIONS));

		queryBuilder.append("(");
		queryBuilder.append(String.format("%s %s, ", DbFieldNames.ID, DbFieldParameters.ID));
		queryBuilder.append(String.format("%s %s, ", DbFieldNames.NAME, DbFieldParameters.NAME));
		queryBuilder.append(String.format("%s %s, ", DbFieldNames.LATITUDE,
			DbFieldParameters.STATION_COORDINATE));
		queryBuilder.append(String.format("%s %s", DbFieldNames.LONGITUDE,
			DbFieldParameters.STATION_COORDINATE));
		queryBuilder.append(")");

		return queryBuilder.toString();
	}

	private String buildRoutesAndStationsTableCreationQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("create table if not exists %s ",
			DbTableNames.ROUTES_AND_STATIONS));

		queryBuilder.append("(");
		queryBuilder.append(String.format("%s %s, ", DbFieldNames.ID, DbFieldParameters.ID));
		queryBuilder.append(String.format("%s %s, ", DbFieldNames.ROUTE_ID,
			DbFieldParameters.FOREIGN_ROUTE_ID));
		queryBuilder.append(String.format("%s %s, ", DbFieldNames.STATION_ID,
			DbFieldParameters.FOREIGN_STATION_ID));
		queryBuilder.append(String.format("%s %s", DbFieldNames.TIME_SHIFT, DbFieldParameters.TIME));
		queryBuilder.append(")");

		return queryBuilder.toString();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldDatabaseVersion, int newDatabaseVersion) {
		db.beginTransaction();

		try {
			dropTables(db);
			createTables(db);

			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
	}

	private void dropTables(SQLiteDatabase db) {
		dropTable(db, DbTableNames.ROUTES_AND_STATIONS);
		dropTable(db, DbTableNames.TRIPS);
		dropTable(db, DbTableNames.ROUTES);
		dropTable(db, DbTableNames.STATIONS);
	}

	private void dropTable(SQLiteDatabase db, String tableName) {
		db.execSQL(String.format("drop table %s", tableName));
	}
}
