package app.android.bustime.local;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
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

	public void insertDepartureTime(Time time) {
		database.beginTransaction();

		try {
			tryInsertDepartureTime(time);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void tryInsertDepartureTime(Time time) {
		ContentValues databaseValues = new ContentValues();

		databaseValues.put(DbFieldNames.ROUTE_ID, id);
		databaseValues.put(DbFieldNames.DEPARTURE_TIME, time.toString());

		database.insert(DbTableNames.TRIPS, null, databaseValues);
	}

	public void removeDepartureTime(Time time) {
		database.beginTransaction();

		try {
			tryRemoveDepartureTime(time);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void tryRemoveDepartureTime(Time time) {
		database.delete(DbTableNames.TRIPS,
			String.format("%s = %s", DbFieldNames.DEPARTURE_TIME, time.toString()), null);
	}

	public List<Time> getDepartureTimetable() {
		List<Time> departureTimetable = new ArrayList<Time>();

		Cursor databaseCursor = database.rawQuery(buildDepertureTimetableSelectionQuery(), null);

		while (databaseCursor.moveToNext()) {
			String timeAsString = extractTimeFromCursor(databaseCursor);
			departureTimetable.add(new Time(timeAsString));
		}

		return departureTimetable;
	}

	private String buildDepertureTimetableSelectionQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");

		queryBuilder.append(String.format("%s ", DbFieldNames.DEPARTURE_TIME));

		queryBuilder.append(String.format("from %s ", DbTableNames.TRIPS));
		queryBuilder.append(String.format("where %s = %d", DbFieldNames.ROUTE_ID, id));

		return queryBuilder.toString();
	}

	private String extractTimeFromCursor(Cursor databaseCursor) {
		return databaseCursor.getString(databaseCursor
			.getColumnIndexOrThrow(DbFieldNames.DEPARTURE_TIME));
	}
}
