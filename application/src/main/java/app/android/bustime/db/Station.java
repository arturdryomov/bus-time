package app.android.bustime.db;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;


public class Station implements Parcelable
{
	private static final int SPECIAL_PARCELABLE_OBJECTS_BITMASK = 0;

	private final SQLiteDatabase database;

	private long id;
	private String name;
	private double latitude;
	private double longitude;

	Station(ContentValues databaseValues) {
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

		Double latitudeAsDouble = databaseValues.getAsDouble(DbFieldNames.LATITUDE);
		if (latitudeAsDouble == null) {
			throw new DbException();
		}
		latitude = latitudeAsDouble.doubleValue();

		Double longitudeAsDouble = databaseValues.getAsDouble(DbFieldNames.LONGITUDE);
		if (longitudeAsDouble == null) {
			throw new DbException();
		}
		longitude = longitudeAsDouble.doubleValue();
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
		Time routeTimeShift = getRouteTimeShift(route);
		List<Time> routeDepartureTimetable = route.getFullWeekDepartureTimetable();

		return getRouteTimetable(routeTimeShift, routeDepartureTimetable);
	}

	private Time getRouteTimeShift(Route route) {
		Cursor databaseCursor = database.rawQuery(buildRouteTimeShiftSelectionQuery(route), null);

		databaseCursor.moveToFirst();
		Time shiftTime = Time.parse(extractTimeShiftFromCursor(databaseCursor));

		databaseCursor.close();

		return shiftTime;
	}

	private String buildRouteTimeShiftSelectionQuery(Route route) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");
		queryBuilder.append(String.format("%s ", DbFieldNames.TIME_SHIFT));

		queryBuilder.append(String.format("from %s ", DbTableNames.ROUTES_AND_STATIONS));

		queryBuilder.append(String.format("where %s = %d and ", DbFieldNames.STATION_ID, id));
		queryBuilder.append(String.format("%s = %d", DbFieldNames.ROUTE_ID, route.getId()));

		return queryBuilder.toString();
	}

	private String extractTimeShiftFromCursor(Cursor databaseCursor) {
		int timeShiftColumnIndex = databaseCursor.getColumnIndex(DbFieldNames.TIME_SHIFT);
		return databaseCursor.getString(timeShiftColumnIndex);
	}

	private List<Time> getRouteTimetable(Time routeTimeShift, List<Time> routeDepartureTimetable) {
		List<Time> routeTimetable = new ArrayList<Time>();

		for (Time departureTime : routeDepartureTimetable) {
			routeTimetable.add(departureTime.sum(routeTimeShift));
		}

		return routeTimetable;
	}

	public List<Time> getWorkdaysTimetable(Route route) {
		Time routeTimeShift = getRouteTimeShift(route);
		List<Time> routeDepartureTimetable = route.getWorkdaysDepartureTimetable();

		return getRouteTimetable(routeTimeShift, routeDepartureTimetable);
	}

	public List<Time> getWeekendTimetable(Route route) {
		Time routeTimeShift = getRouteTimeShift(route);
		List<Time> routeDepartureTimetable = route.getWeekendDepartureTimetable();

		return getRouteTimetable(routeTimeShift, routeDepartureTimetable);
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
		database = DbProvider.getInstance().getDatabase();

		readStationDataFromParcel(parcel);
	}

	private void readStationDataFromParcel(Parcel parcel) {
		id = parcel.readLong();
		name = parcel.readString();
	}
}
