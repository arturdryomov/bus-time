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
	private static final int SPECIAL_PARCELABLE_OBJECTS_BITMASK = 0;

	private final SQLiteDatabase database;

	private long id;
	private String name;

	Route(ContentValues databaseValues) {
		database = DbProvider.getInstance().getDatabase();

		setValues(databaseValues);
	}

	private void setValues(ContentValues databaseValues) {
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
		return databaseCursor.getString(
			databaseCursor.getColumnIndexOrThrow(DbFieldNames.DEPARTURE_TIME));
	}

	@Override
	public int describeContents() {
		return SPECIAL_PARCELABLE_OBJECTS_BITMASK;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeString(name);
	}

	public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>()
	{
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

		readRouteDataFromParcel(parcel);
	}

	private void readRouteDataFromParcel(Parcel parcel) {
		id = parcel.readLong();
		name = parcel.readString();
	}
}
