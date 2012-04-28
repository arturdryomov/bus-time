package app.android.bustime.local;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;


public class Station
{
	private final SQLiteDatabase database;
	private final Stations stations;

	private long id;
	private String name;

	Station(ContentValues databaseValues) {
		database = DbProvider.getInstance().getDatabase();
		stations = DbProvider.getInstance().getStations();

		setStationValues(databaseValues);
	}

	private void setStationValues(ContentValues databaseValues) {
		Long idAsLong = databaseValues.getAsLong(DbFieldNames.ID);
		if (idAsLong == null) {
			throw new DbException();
		}
		id = idAsLong.longValue();

		String nameAsString = databaseValues.getAsString(DbFieldNames.NAME);
		if (nameAsString == null) {
			throw new DbException();
		}
		name = nameAsString;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name.equals(this.name)) {
			return;
		}

		database.beginTransaction();
		try {
			trySetName(name);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void trySetName(String name) {
		if (stations.isStationExist(name)) {
			throw new AlreadyExistsException();
		}

		updateName(name);
		this.name = name;
	}

	private void updateName(String name) {
		ContentValues databaseValues = new ContentValues();
		databaseValues.put(DbFieldNames.NAME, name);

		database.update(DbTableNames.STATIONS, databaseValues,
			String.format("%s = %d", DbFieldNames.ID, id), null);
	}
}
