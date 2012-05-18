package app.android.bustime.db;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;


public class Route implements Parcelable
{
	private final SQLiteDatabase database;
	private final Routes routes;

	private long id;
	private String name;

	Route(SQLiteDatabase database, Routes routes, ContentValues databaseValues) {
		this.database = database;
		this.routes = routes;

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

	/**
	 * @throws AlreadyExistsException if route with such name already exists.
	 */
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

	/**
	 * @throws AlreadyExistsException if such departure time already exists.
	 */
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
		if (isDepartureTimeExist(time)) {
			throw new AlreadyExistsException();
		}

		ContentValues databaseValues = new ContentValues();

		databaseValues.put(DbFieldNames.ROUTE_ID, id);
		databaseValues.put(DbFieldNames.DEPARTURE_TIME, time.toString());

		database.insert(DbTableNames.TRIPS, null, databaseValues);
	}

	private boolean isDepartureTimeExist(Time time) {
		Cursor databaseCursor = database.rawQuery(buildDepartureTimeCountQuery(time.toString()), null);
		databaseCursor.moveToFirst();

		final int DEPARTURE_TIMES_COUNT_COLUMN_INDEX = 0;
		int departureTimesCount = databaseCursor.getInt(DEPARTURE_TIMES_COUNT_COLUMN_INDEX);

		boolean isDepartureTimeExist = departureTimesCount > 0;

		databaseCursor.close();

		return isDepartureTimeExist;
	}

	private String buildDepartureTimeCountQuery(String timeAsString) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select count(*) ");
		queryBuilder.append(String.format("from %s ", DbTableNames.TRIPS));
		queryBuilder.append(String.format("where %s = %d and %s = '%s'", DbFieldNames.ROUTE_ID, id,
			DbFieldNames.DEPARTURE_TIME, timeAsString));

		return queryBuilder.toString();
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
			String.format("%s = '%s'", DbFieldNames.DEPARTURE_TIME, time.toString()), null);
	}

	public List<Time> getDepartureTimetable() {
		List<Time> departureTimetable = new ArrayList<Time>();

		Cursor databaseCursor = database.rawQuery(buildDepartureTimetableSelectionQuery(), null);

		while (databaseCursor.moveToNext()) {
			String timeAsString = extractTimeFromCursor(databaseCursor);
			departureTimetable.add(new Time(timeAsString));
		}

		databaseCursor.close();

		return departureTimetable;
	}

	private String buildDepartureTimetableSelectionQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");

		queryBuilder.append(String.format("%s ", DbFieldNames.DEPARTURE_TIME));

		queryBuilder.append(String.format("from %s ", DbTableNames.TRIPS));
		queryBuilder.append(String.format("where %s = %d ", DbFieldNames.ROUTE_ID, id));
		queryBuilder.append(String.format("order by %s", DbFieldNames.DEPARTURE_TIME));

		return queryBuilder.toString();
	}

	private String extractTimeFromCursor(Cursor databaseCursor) {
		return databaseCursor.getString(databaseCursor
			.getColumnIndexOrThrow(DbFieldNames.DEPARTURE_TIME));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeString(name);
	}

	public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
		@Override
		public Route createFromParcel(Parcel parcel) {
			return new Route(parcel);
		}

		@Override
		public Route[] newArray(int size) {
			return new Route[size];
		}
	};

	private Route(Parcel parcel) {
		database = DbProvider.getInstance().getDatabase();
		routes = DbProvider.getInstance().getRoutes();

		readRouteDataFromParcel(parcel);
	}

	private void readRouteDataFromParcel(Parcel parcel) {
		id = parcel.readLong();
		name = parcel.readString();
	}
}
