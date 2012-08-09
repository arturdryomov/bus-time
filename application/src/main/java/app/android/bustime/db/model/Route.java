package app.android.bustime.db.model;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import app.android.bustime.db.DbException;
import app.android.bustime.db.DbProvider;
import app.android.bustime.db.sqlite.DbFieldNames;
import app.android.bustime.db.sqlite.DbFieldValues;
import app.android.bustime.db.sqlite.DbTableNames;
import app.android.bustime.db.time.Time;


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

	public boolean isWeekPartDependent() {
		return getCountQueryResult(buildFullWeekRouteTripsCountingQuery()) == 0;
	}

	private String buildFullWeekRouteTripsCountingQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select count(*) ");

		queryBuilder.append(String.format("from %s ", DbTableNames.TRIPS));

		queryBuilder.append(String.format("where %s = %d and ", DbFieldNames.ROUTE_ID, id));
		queryBuilder.append(
			String.format("%s = %d", DbFieldNames.TRIP_TYPE_ID, DbFieldValues.TRIP_FULL_WEEK_ID));

		return queryBuilder.toString();
	}

	private long getCountQueryResult(String countQuery) {
		return DatabaseUtils.longForQuery(database, countQuery, null);
	}

	public List<Time> getFullWeekDepartureTimetable() {
		return getDepartureTimetable(DbFieldValues.TRIP_FULL_WEEK_ID);
	}

	private List<Time> getDepartureTimetable(int tripTypeId) {
		List<Time> departureTimetable = new ArrayList<Time>();

		Cursor databaseCursor = database.rawQuery(buildDepartureTimetableSelectionQuery(tripTypeId),
			null);

		while (databaseCursor.moveToNext()) {
			String departureTimeAsString = extractDepartureTimeFromCursor(databaseCursor);
			departureTimetable.add(Time.parse(departureTimeAsString));
		}

		databaseCursor.close();

		return departureTimetable;
	}

	private String buildDepartureTimetableSelectionQuery(int tripTypeId) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");
		queryBuilder.append(String.format("%s ", DbFieldNames.DEPARTURE_TIME));

		queryBuilder.append(String.format("from %s ", DbTableNames.TRIPS));

		queryBuilder.append(String.format("where %s = %d and ", DbFieldNames.ROUTE_ID, id));
		queryBuilder.append(String.format("%s = %d ", DbFieldNames.TRIP_TYPE_ID, tripTypeId));

		queryBuilder.append(String.format("order by %s", DbFieldNames.DEPARTURE_TIME));

		return queryBuilder.toString();
	}

	private String extractDepartureTimeFromCursor(Cursor databaseCursor) {
		int departureTimeColumnIndex = databaseCursor.getColumnIndex(DbFieldNames.DEPARTURE_TIME);
		return databaseCursor.getString(departureTimeColumnIndex);
	}

	public List<Time> getWorkdaysDepartureTimetable() {
		return getDepartureTimetable(DbFieldValues.TRIP_WORKDAY_ID);
	}

	public List<Time> getWeekendDepartureTimetable() {
		return getDepartureTimetable(DbFieldValues.TRIP_WEEKEND_ID);
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
