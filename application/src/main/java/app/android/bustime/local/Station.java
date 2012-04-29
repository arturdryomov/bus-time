package app.android.bustime.local;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;


public class Station implements Parcelable
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

	public void insertShiftTimeForRoute(Route route, Time time) {
		database.beginTransaction();

		try {
			tryInsertShiftTimeForRoute(route, time);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void tryInsertShiftTimeForRoute(Route route, Time time) {
		ContentValues databaseValues = new ContentValues();

		databaseValues.put(DbFieldNames.ROUTE_ID, route.getId());
		databaseValues.put(DbFieldNames.STATION_ID, id);
		databaseValues.put(DbFieldNames.TIME_SHIFT, time.toString());

		database.insert(DbTableNames.ROUTES_AND_STATIONS, null, databaseValues);
	}

	public void removeShiftTimeForRoute(Route route, Time time) {
		database.beginTransaction();

		try {
			tryRemoveShiftTimeForRoute(route, time);
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void tryRemoveShiftTimeForRoute(Route route, Time time) {
		database.delete(DbTableNames.ROUTES_AND_STATIONS, String.format("%s = %d and %s = '%s'",
			DbFieldNames.ROUTE_ID, route.getId(), DbFieldNames.TIME_SHIFT, time.toString()), null);
	}

	public List<Time> getTimetableForRoute(Route route) {
		List<Time> routeTimetable = new ArrayList<Time>();

		Cursor databaseCursor = database.rawQuery(buildRouteTimetableSelectionQuery(route), null);

		while (databaseCursor.moveToNext()) {
			String timeAsString = extractTimeFromCursor(databaseCursor);
			// TODO: Append time or decide where to do it
			routeTimetable.add(new Time(timeAsString));
		}

		return routeTimetable;
	}

	private String buildRouteTimetableSelectionQuery(Route route) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");

		queryBuilder.append(String.format("%s ", DbFieldNames.TIME_SHIFT));

		queryBuilder.append(String.format("from %s ", DbTableNames.ROUTES_AND_STATIONS));
		queryBuilder.append(String.format("where %s = %d and %s = %d", DbFieldNames.STATION_ID, id,
			DbFieldNames.ROUTE_ID, route.getId()));

		return queryBuilder.toString();
	}

	private String extractTimeFromCursor(Cursor databaseCursor) {
		return databaseCursor.getString(databaseCursor.getColumnIndexOrThrow(DbFieldNames.TIME_SHIFT));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel destinationParcel, int flags) {
		destinationParcel.writeLong(id);
		destinationParcel.writeString(name);
	}

	private static final Parcelable.Creator<Station> CREATOR = new Parcelable.Creator<Station>() {
		@Override
		public Station createFromParcel(Parcel sourceParcel) {
			return new Station(sourceParcel);
		};

		@Override
		public Station[] newArray(int size) {
			return new Station[size];
		};
	};

	private Station(Parcel parcel) {
		database = DbProvider.getInstance().getDatabase();
		stations = DbProvider.getInstance().getStations();
	}

	public void readStationDataFromParcel(Parcel parcel) {
		id = parcel.readLong();
		name = parcel.readString();
	}
}
