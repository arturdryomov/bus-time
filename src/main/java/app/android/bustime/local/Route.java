package app.android.bustime.local;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;


public class Route
{
	private final SQLiteDatabase database;
	private final Routes routes;

	private long id;
	private String name;

	Route(ContentValues databaseValues) {
		database = DbProvider.getInstance().getDatabase();
		routes = DbProvider.getInstance().getRoutes();

		setRouteValues(databaseValues);
	}

	private void setRouteValues(ContentValues databaseValues) {
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
		if (routes.isRouteExist(name)) {
			throw new AlreadyExistsException();
		}

		updateName(name);
		this.name = name;
	}

	private void updateName(String name) {
		ContentValues databaseValues = new ContentValues();
		databaseValues.put(DbFieldNames.NAME, name);

		database.update(DbTableNames.ROUTES, databaseValues,
			String.format("%s = %d", DbFieldNames.ID, id), null);
	}
}
