package ru.ming13.bustime.db.model;


import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.os.Parcel;
import android.os.Parcelable;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.sqlite.DbSchema;
import ru.ming13.bustime.db.time.Time;
import ru.ming13.bustime.db.time.TimeException;


public class Station implements Parcelable
{
	private static final int SPECIAL_PARCELABLE_OBJECTS_BITMASK = 0;

	private final long id;
	private final String name;
	private final double latitude;
	private final double longitude;

	Station(long id, String name, double latitude, double longitude) {
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public List<Time> getFullWeekTimetable(Route route) {
		List<Time> routeDepartureTimetable = route.getFullWeekDepartureTimetable();

		return getTimetable(route, routeDepartureTimetable);
	}

	private List<Time> getTimetable(Route route, List<Time> routeDepartureTimetable) {
		Time routeTimeShift = getRouteTimeShift(route);
		List<Time> timetable = new ArrayList<Time>();

		for (Time departureTime : routeDepartureTimetable) {
			timetable.add(departureTime.sum(routeTimeShift));
		}

		return timetable;
	}

	private Time getRouteTimeShift(Route route) {
		SQLiteDatabase database = DbProvider.getInstance().getDatabase();
		Cursor databaseCursor = database.rawQuery(buildRouteTimeShiftSelectionQuery(route), null);

		databaseCursor.moveToFirst();
		Time shiftTime = Time.parse(extractTimeShiftFromCursor(databaseCursor));

		databaseCursor.close();

		return shiftTime;
	}

	private String buildRouteTimeShiftSelectionQuery(Route route) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");
		queryBuilder.append(String.format("%s ", DbSchema.RoutesAndStationsColumns.TIME_SHIFT));

		queryBuilder.append(String.format("from %s ", DbSchema.Tables.ROUTES_AND_STATIONS));

		queryBuilder.append(
			String.format("where %s = %d and ", DbSchema.RoutesAndStationsColumns.STATION_ID, id));
		queryBuilder.append(
			String.format("%s = %d", DbSchema.RoutesAndStationsColumns.ROUTE_ID, route.getId()));

		return queryBuilder.toString();
	}

	private String extractTimeShiftFromCursor(Cursor databaseCursor) {
		int timeShiftColumnIndex = databaseCursor.getColumnIndex(
			DbSchema.RoutesAndStationsColumns.TIME_SHIFT);
		return databaseCursor.getString(timeShiftColumnIndex);
	}

	public List<Time> getWorkdaysTimetable(Route route) {
		List<Time> routeDepartureTimetable = route.getWorkdaysDepartureTimetable();

		return getTimetable(route, routeDepartureTimetable);
	}

	public List<Time> getWeekendTimetable(Route route) {
		List<Time> routeDepartureTimetable = route.getWeekendDepartureTimetable();

		return getTimetable(route, routeDepartureTimetable);
	}

	public Time getClosestFullWeekBusTime(Route route) {
		return getClosestBusTime(route, DbSchema.TripTypesColumnsValues.FULL_WEEK_ID);
	}

	private Time getClosestBusTime(Route route, int tripTypeId) {
		try {
			Time routeTimeShift = getRouteTimeShift(route);

			String closestDepartureTimeSelectionQuery = buildClosestDepartureTimeSelectionQuery(
				route.getId(), routeTimeShift, tripTypeId);

			SQLiteDatabase database = DbProvider.getInstance().getDatabase();
			String closestDepartureStringTime = DatabaseUtils.stringForQuery(database,
				closestDepartureTimeSelectionQuery, null);
			Time closestDepartureTime = Time.parse(closestDepartureStringTime);

			return closestDepartureTime.sum(routeTimeShift);
		}
		catch (SQLiteDoneException e) {
			throw new TimeException();
		}
	}

	private String buildClosestDepartureTimeSelectionQuery(long routeId, Time routeTimeShift, int tripTypeId) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");
		queryBuilder.append(String.format("%s ", DbSchema.TripsColumns.DEPARTURE_TIME));

		queryBuilder.append(String.format("from %s ", DbSchema.Tables.TRIPS));

		queryBuilder.append(
			String.format("where %s = %d and ", DbSchema.TripsColumns.ROUTE_ID, routeId));
		queryBuilder.append(
			String.format("%s = %d and ", DbSchema.TripsColumns.TRIP_TYPE_ID, tripTypeId));
		queryBuilder.append(String.format("%s >= '%s' ", DbSchema.TripsColumns.DEPARTURE_TIME,
			calculatePossibleDepartureTime(routeTimeShift).toDatabaseString()));

		queryBuilder.append(String.format("order by %s ", DbSchema.TripsColumns.DEPARTURE_TIME));

		queryBuilder.append("limit 1");

		return queryBuilder.toString();
	}

	private Time calculatePossibleDepartureTime(Time routeTimeShift) {
		Time currentTime = Time.newInstance();
		Time possibleDepartureTime = currentTime.subtract(routeTimeShift);

		if (possibleDepartureTime.isAfter(currentTime)) {
			// Handle jump into previous day
			possibleDepartureTime = Time.parse("00:00");
		}

		return possibleDepartureTime;
	}

	public Time getClosestWorkdaysBusTime(Route route) {
		return getClosestBusTime(route, DbSchema.TripTypesColumnsValues.WORKDAY_ID);
	}

	public Time getClosestWeekendBusTime(Route route) {
		return getClosestBusTime(route, DbSchema.TripTypesColumnsValues.WEEKEND_ID);
	}

	@Override
	public int describeContents() {
		return SPECIAL_PARCELABLE_OBJECTS_BITMASK;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeString(name);
		parcel.writeDouble(latitude);
		parcel.writeDouble(longitude);
	}

	public static final Parcelable.Creator<Station> CREATOR = new Parcelable.Creator<Station>()
	{
		@Override
		public Station createFromParcel(Parcel parcel) {
			return new Station(parcel);
		}

		@Override
		public Station[] newArray(int size) {
			return new Station[size];
		}
	};

	private Station(Parcel parcel) {
		id = parcel.readLong();
		name = parcel.readString();
		latitude = parcel.readDouble();
		longitude = parcel.readDouble();
	}
}
