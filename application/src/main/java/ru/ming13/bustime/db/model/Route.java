package ru.ming13.bustime.db.model;


import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.sqlite.DbSchema;
import ru.ming13.bustime.db.time.Time;


public class Route implements Parcelable
{
	private static final int SPECIAL_PARCELABLE_OBJECTS_BITMASK = 0;

	private final long id;
	private final String name;

	Route(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isWeekPartDependent() {
		return getCountingQueryResult(buildFullWeekRouteTripsCountingQuery()) == 0;
	}

	private String buildFullWeekRouteTripsCountingQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select count(*) ");

		queryBuilder.append(String.format("from %s ", DbSchema.Tables.TRIPS));

		queryBuilder.append(String.format("where %s = %d and ", DbSchema.TripsColumns.ROUTE_ID, id));
		queryBuilder.append(String.format("%s = %d", DbSchema.TripsColumns.TRIP_TYPE_ID,
			DbSchema.TripTypesColumnsValues.FULL_WEEK_ID));

		return queryBuilder.toString();
	}

	private long getCountingQueryResult(String countingQuery) {
		SQLiteDatabase database = DbProvider.getInstance().getDatabase();

		return DatabaseUtils.longForQuery(database, countingQuery, null);
	}

	public List<Time> getFullWeekDepartureTimetable() {
		return getDepartureTimetable(DbSchema.TripTypesColumnsValues.FULL_WEEK_ID);
	}

	private List<Time> getDepartureTimetable(int tripTypeId) {
		SQLiteDatabase database = DbProvider.getInstance().getDatabase();
		Cursor databaseCursor = database.rawQuery(buildDepartureTimetableSelectionQuery(tripTypeId),
			null);

		List<Time> departureTimetable = new ArrayList<Time>();

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
		queryBuilder.append(String.format("%s ", DbSchema.TripsColumns.DEPARTURE_TIME));

		queryBuilder.append(String.format("from %s ", DbSchema.Tables.TRIPS));

		queryBuilder.append(String.format("where %s = %d and ", DbSchema.TripsColumns.ROUTE_ID, id));
		queryBuilder.append(String.format("%s = %d ", DbSchema.TripsColumns.TRIP_TYPE_ID, tripTypeId));

		queryBuilder.append(String.format("order by %s", DbSchema.TripsColumns.DEPARTURE_TIME));

		return queryBuilder.toString();
	}

	private String extractDepartureTimeFromCursor(Cursor databaseCursor) {
		int departureTimeColumnIndex = databaseCursor.getColumnIndex(
			DbSchema.TripsColumns.DEPARTURE_TIME);
		return databaseCursor.getString(departureTimeColumnIndex);
	}

	public List<Time> getWorkdaysDepartureTimetable() {
		return getDepartureTimetable(DbSchema.TripTypesColumnsValues.WORKDAY_ID);
	}

	public List<Time> getWeekendDepartureTimetable() {
		return getDepartureTimetable(DbSchema.TripTypesColumnsValues.WEEKEND_ID);
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
		id = parcel.readLong();
		name = parcel.readString();
	}
}
